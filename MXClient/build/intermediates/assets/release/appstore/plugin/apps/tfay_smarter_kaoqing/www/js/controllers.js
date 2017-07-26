angular.module('starter.controllers', [])
    .controller('LocationCtrl',['$state','$stateParams','MXCommonService',function ($state,$stateParams,MXCommonService) {

        MXCommonService.MXCommon.getLocation().then(function (data) {

            MXCommonService.MXCommon.getCurrentUser()
                .then(function (currentUser) {

                    //获取打开历史数据等信息
                    $state.go("redPackage.home",{"location":data,"userInfo":currentUser});
                }, function (error) {

                   console.log(error);
                })
        })
     }])
    .controller('HomeCtrl', ['$rootScope','$scope', '$state', '$stateParams','$ionicHistory','MXCommonService','RestfulResourcesAddressService',
        '$interpolate', 'RestfulResources','$timeout','$HttpResource','RestfulResourceAddress','RestfulResources','$ionicPopup','$timeout',
        function ($rootScope,$scope, $state,$stateParams, $ionicHistory,MXCommonService,RestfulResourcesAddressService,
                  $interpolate,RestfulResources,$timeout,$HttpResource,RestfulResourceAddress,RestfulResources,$ionicPopup,$timeout) {
            $scope.clockContent = "打卡";
            $scope.clear = $rootScope.enterFlag;
            $scope.userInfo = $stateParams.userInfo;
            $scope.locationData = JSON.parse($stateParams.location);
            $scope.inClockArea = true;
            $scope.canSignIn = true;
            $scope.showClockedContent = false;
            $scope.showHaveClocked = false;
            $scope.refreshComplete = '';

            var requestUrl = RestfulResourceAddress+RestfulResources.ATTANDANCE_INFO;
            var _userAccount = '18080123123';

            window.addEventListener("message",receiveMessage,false);

            //请求员工的出勤情况，参考打卡位置，个人排行榜，部门排行榜
            function getAttendanceInfo(type) {

                $HttpResource.post(requestUrl,{userAccount:$scope.userInfo.login_name,userId:$scope.userInfo.id}).then(function (data) {
                    $scope.employeeInfo = JSON.parse(data.result);

                    $scope.currentDateYear = $scope.employeeInfo.retAttendanceVO.countDate.substr(0,4),
                    $scope.currentDateMonth = $scope.employeeInfo.retAttendanceVO.countDate.substr(4,2),
                    $scope.currentDateDay = $scope.employeeInfo.retAttendanceVO.countDate.substr(6,2),
                    $scope.currentDateMonthNoZero = $scope.currentDateMonth>10 ? $scope.currentDateMonth:$scope.currentDateMonth.substr(1,1)

                    $scope.employeeTop = $scope.employeeInfo.retAttendanceVO.employeeTop;
                    $scope.departmentTop = $scope.employeeInfo.retAttendanceVO.departmentTop;

                    $scope.canSignIn = ($scope.employeeInfo.retAttendanceVO.canSignIn == 'Y')?true:false;
                    $scope.signInType = $scope.employeeInfo.retAttendanceVO.signInType == "1"?"上午":"下午";

                    refreshTime($scope.employeeInfo.currTimeMillis);
                    refreshLocationData();

                    //下拉刷新时，获取数据后成功后，滚动条恢复原样
                    if(type == "Refresh"){
                        $scope.$broadcast('scroll.refreshComplete');
                        $scope.refreshComplete = '刷新成功';
                        postLocationMessage($scope.currentlocation,"Refresh");
                        $timeout(function () {
                            $scope.refreshComplete = '';
                        },1000);
                    }else{
                        postLocationMessage($scope.currentlocation,"onload");
                    }
                },function (err) {
                    var _message = "获取员工考勤信息失败";
                    showReturnMessage(_message);

                });
            }



            //测试次数
            $scope.count = '8';
            $scope.$on('$ionicView.beforeEnter', function (e) {

                getAttendanceInfo("beforeEnterView");

            });

            //传输数据
            function postLocationMessage(data,type) {
                var iframe = document.getElementById("mapFrame").contentWindow;
                (type == "onload")? document.getElementById('mapFrame').onload =function () {iframe.postMessage(data, '*');}:iframe.postMessage(data, '*');

            };

            //点击打卡按钮打卡
            $scope.clock = function () {

                if($scope.canSignIn == false){
                    var _message = "您已打卡";
                    showReturnMessage(_message);
                    return;
                }

                MXCommonService.MXCommon.getLocation().then(function (data) {
                    $scope.locationData = JSON.parse(data);
                    refreshLocationData();
                    $scope._distance = getDistance($scope.locationData.longitude, $scope.locationData.latitude,104.094645, 30.658494).toFixed(0);
                    $scope.inClockArea =($scope._distance <500)?true:false;
                    $scope.isOutSignIn = ($scope._distance <500)?'N':'Y';

                    $ionicPopup.show({
                        template:
                        '<p ng-show="!inClockArea" style="text-align: center;font-size: 1.1em;color: black">现在距离打卡位置：<span style="color: #f4cd31">{{_distance}}米</span></p>' +
                        '<div style="position: relative" ng-show="inClockArea">' +
                        '<div style="position: absolute;height: 6px;width: 6px;border-radius: 3px;top:6px;background-color: #a2a2a2"></div>' +
                        '<div style="margin-left: 12px;display: inline-block">您的位置在打卡范围内</div>' +
                        '</div>'+
                        '<div style="position: relative" ng-show="_distance == 0">' +
                        '<div style="position: absolute;height: 6px;width: 6px;border-radius: 3px;top:6px;background-color: #a2a2a2"></div>' +
                        '<div style="margin-left: 12px;display: inline-block">未获取到地理位置</div>' +
                        '</div>'+
                        '<div style="position: relative" ng-show="_distance!=0">' +
                        '<div style="position: absolute;height: 6px;width: 6px;border-radius: 3px;top:6px;background-color: #a2a2a2"></div>' +
                        '<div style="margin-left: 12px;display: inline-block">考勤地点：{{address}}</div>' +
                        '</div>'+
                        '<div style="position: relative" ng-show="!inClockArea">' +
                        '<div style="position: absolute;height: 6px;width: 6px;border-radius: 3px;top:6px;background-color: #a2a2a2"></div>' +
                        '<div style="margin-left: 12px;display: inline-block">当前地点不在考勤范围内，是否外勤打卡</div>' +
                        '</div>',
                        title:'考勤打卡',
                        scope:$scope,
                        buttons:[
                            {text:'取消打卡'},
                            {
                                text:'确认打卡',
                                type:'button-tfay',
                                onTap:function (e) {
                                    clockNow();
                                }
                            }
                        ]
                    })

                },function (err) {
                    var _message = "获取地理位置失败";
                    showReturnMessage(_message);

                })



            }

            //向后台请求打卡
            function clockNow() {
                    var _signInlocationData = {
                        referencedLocationData:{
                            longitude:$scope.employeeInfo.attendanceEmployee.attendanceLon,
                            latitude:$scope.employeeInfo.attendanceEmployee.attendanceLan
                        },
                        currentLocationData:$scope.locationData
                    }
                    var _data = {
                        'userAccount':$scope.userInfo.login_name,
                        'signInLon':$scope.locationData.longitude,
                        'signInLan':$scope.locationData.latitude,
                        'signInPosition': $scope.address,
                        'isOutSignIn':$scope.isOutSignIn
                    }

                    var _requestUrl = RestfulResourceAddress+RestfulResources.SIGN_IN;

                    //发起发卡请求
                    $HttpResource.post(_requestUrl,_data).then(function (data) {
                        $scope.signInedInfo = JSON.parse(data.result);
                        $scope.attendanceType = '';
                        switch ($scope.signInedInfo.retAttendanceVO.attendanceType){
                            case'0':$scope.attendanceType = '正常';
                                break;
                            case'1':$scope.attendanceType = '迟到';
                                break;
                            case'2':$scope.attendanceType = '早退';
                                break;
                            case'3':$scope.attendanceType = '迟到并早退';
                                break;
                            case'4':$scope.attendanceType = '旷工';
                                break;
                            case'5':$scope.attendanceType = '缺卡';
                                break;
                            default:break;
                        }
                        var message =$scope.signInType+"打卡成功！状态："+$scope.attendanceType;
                        showReturnMessage(message);
                        getAttendanceInfo("signIn");
                    },function (err) {
                        var message = err != null && err.context != null ? err.context.message:'操作失败!服务器繁忙!';
                        showReturnMessage(message);
                    })
            }

            $scope.doRefresh = function () {

                MXCommonService.MXCommon.getLocation().then(function (data) {
                    $scope.locationData = JSON.parse(data);
                    refreshLocationData();
                    getAttendanceInfo("Refresh");
                },function (err) {
                    var _message = "获取地理位置失败";
                    showReturnMessage(_message);
                })

            }

            // 打卡成功后提示信息
            function showReturnMessage(message) {
                $scope.showFlag = true;
                $scope.showMessage = message;
                $timeout(function () {
                    $scope.showFlag =!$scope.showFlag;
                },3000);

            }

            //接收地图解析出的地址信息
            function receiveMessage(event) {
               $scope.address = event.data;
            }

            function refreshLocationData() {
                $scope.currentlocation = {
                    referencedLocationData:{
                        longitude:$scope.employeeInfo.attendanceEmployee.attendanceLon,
                        latitude:$scope.employeeInfo.attendanceEmployee.attendanceLan
                    },
                    currentLocationData:$scope.locationData
                }
            }

            //计算距离
            function getDistance(longitude1, latitude1, longitude2, latitude2) {
                // 维度
                lat1 = (Math.PI / 180) * latitude1;
                lat2 = (Math.PI / 180) * latitude2;

                // 经度
                lon1 = (Math.PI / 180) * longitude1;
                lon2 = (Math.PI / 180) * longitude2;

                // 地球半径
                R = 6378.137;

                // 两点间距离 km，如果想要米的话，结果*1000就可以了
                d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1))
                    * R;
                return d * 1000;
            }

            //动态时间
            function refreshTime(_currentTime) {
                var currDate = new Date();
                currDate.setTime(_currentTime * 1000);

                var _hours = currDate.getHours();var _minutes = currDate.getMinutes();var _seconds = currDate.getSeconds();

                $scope.nowDate = (_hours >=10 ? _hours : '0'+ _hours )+':'+(_minutes >= 10  ?  _minutes : '0' + _minutes )+ ':' + (_seconds >= 10 ? _seconds : '0'+_seconds );

                $scope.setTimer=function(){
                    $scope.$apply(function(){

                        if( parseInt(_seconds)  == 59 ) {
                            _seconds = 0;_minutes = _minutes + 1;
                            if( _minutes > 59){
                                _minutes = 0;_hours += 1;
                                if(_hours > 23){ _hours = 0;}
                            }
                        }else {
                            _seconds += 1;
                        }
                        $scope.nowDate = (_hours >=10 ? _hours : '0'+ _hours )+':'+(_minutes >= 10  ?  _minutes : '0' + _minutes )+ ':' + (_seconds >= 10 ? _seconds : '0'+_seconds );
                    });
                };
                //每隔1秒刷新一次时间
                $scope.setTimerInterval=setInterval($scope.setTimer,1000);
            }



        }]);
