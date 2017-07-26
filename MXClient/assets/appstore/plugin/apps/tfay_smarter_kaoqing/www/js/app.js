// Ionic Starter App

// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
// 'starter.services' is found in services.js
// 'starter.controllers' is found in controllers.js

angular.module('starter', ['ionic', 'ngIOS9UIWebViewPatch', 'starter.controllers', 'starter.services'])

    .run(['$ionicPlatform', '$rootScope', '$ionicHistory',
        function ($ionicPlatform, $rootScope, $ionicHistory) {
            var _enterFlag = window.location.href;
            $rootScope.enterFlag = _enterFlag.indexOf('?') > 0 ? true :false;

            $ionicPlatform.ready(function () {

                $rootScope.$ionicPlatform = ionic.Platform;

                $rootScope.closeWindow = function () {
                    MXWebui.closeWindow();
                };

                $rootScope.MXWebUIBack = function () {
                    if ($ionicHistory.backView()) {
                        $ionicHistory.goBack();
                    } else {
                        MXWebui.closeWindow();
                    }
                }
            });
        }])
    //理由级别限制
    .run(['$ionicPlatform', '$rootScope', '$state', '$location',  '$ionicHistory',
        function ($ionicPlatform, $rootScope, $state, $location, $ionicHistory) {
            var flag = true;
            //权限路由限制
            $rootScope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
                //event.preventDefault();

                if($ionicHistory.backView() != null & $ionicHistory.backView().stateId == 'redPackage.lcation') {
                    MXWebui.closeWindow();
                }

            });

        }])
    .run([
        '$rootScope',
        '$ionicPlatform',
        '$ionicHistory',
        'IONIC_BACK_PRIORITY',
        function ($rootScope, $ionicPlatform, $ionicHistory, IONIC_BACK_PRIORITY) {

            function onHardwareBackButton(e) {
                var backView = $ionicHistory.backView();
                if (backView) {
                    backView.go();
                } else {
                    MXWebui.closeWindow();
                }
                e.preventDefault();
                return false;
            }

            $ionicPlatform.registerBackButtonAction(
                onHardwareBackButton,
                IONIC_BACK_PRIORITY.view
            );

        }])
    .config(['$stateProvider', '$urlRouterProvider', '$ionicConfigProvider',
        function ($stateProvider, $urlRouterProvider, $ionicConfigProvider) {

            //配置ionic平台相关参数 主要针对IOS Android

            $ionicConfigProvider.platform.ios.navBar.alignTitle('center');
            $ionicConfigProvider.platform.android.navBar.alignTitle('center');

            $ionicConfigProvider.platform.ios.backButton.previousTitleText(true);
            $ionicConfigProvider.platform.android.backButton.previousTitleText(true);

        }])

    .constant("RestfulResourceAddress", "http://etest.cgnb.cn:8146")

    .constant("RestfulResources", {
        "CREATE": "/hongbao/createHongbao.json",
        "QUERY_HONGBAO": "/hongbao/queryHongbao/{{hongbaoId}}/{{currentUserId}}.json",
        "EXECUTE_GET_HB": "/hongbao/executeGetHB/{{hongbaoId}}/{{currentUserId}}.json",
        "QUERY_USER_GET_RECORDS": "/hongbao/queryUserGetRecords/{{userId}}.json",
        "QUERY_MYSEND_HONGBAO": "/hongbao/queryMySendHongbao/{{userId}}.json",
        "QUERY_HONGBAO_GRAB": "/hongbao/queryHongbaoGrab/{{hongbaoId}}.json",
         "USER_INFO": "",
        "ATTANDANCE_INFO":"/tfay/ws/v2/account/attendance/attendanceInfo",
        "SIGN_IN":"/tfay/ws/v2/account/attendance/signIn"
    })

    .constant("MXAPP", {
        ID: "ZSMARTER_HONGBAO"
    })

    .config(["$stateProvider", "$urlRouterProvider",
        function ($stateProvider, $urlRouterProvider) {


            $stateProvider

                .state('redPackage', {
                    url: '/redPackage',
                    abstract: true,
                    resolve: {}
                })


                .state('redPackage.home', {
                    url: '/home',
                    params: {
                        clear: false,
                        location:null,
                        userInfo:null
                    },
                    views: {
                        '@': {
                            templateUrl: 'templates/home.html',
                            controller: 'HomeCtrl'
                        }
                    }
                })
                .state('redPackage.lcation', {
                    url: '/lcation',
                    params: {
                        clear: false,
                    },
                    views: {
                        '@': {
                            templateUrl: 'templates/location.html',
                            controller: 'LocationCtrl'
                        }
                    }
                })

            $urlRouterProvider.otherwise('/redPackage/lcation');

        }]);
