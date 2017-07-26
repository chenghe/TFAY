angular.module('starter.services', [])

    .factory("RestfulResourcesAddressService", ['RestfulResourceAddress', 'RestfulResources',
        function (RestfulResourceAddress, RestfulResources) {

            var restfulResourceAddress = RestfulResourceAddress;

            return {
                setRestfulResourceAddress: function (address) {
                    restfulResourceAddress = address;
                },
                getRestfulResourceAddress: function () {
                    return restfulResourceAddress;
                }
            };

        }])

    .factory("$HttpResource", ['$q', '$timeout', '$http', '$httpParamSerializerJQLike', '$ionicLoading',
        function ($q, $timeout, $http, $httpParamSerializerJQLike, $ionicLoading) {

            function httpResource(type, url, params, isShowLoading) {
                var defered = $q.defer();

                if (null == isShowLoading) {
                    isShowLoading = true;
                }

                isShowLoading = isShowLoading && true;

                if (isShowLoading) {
                    $ionicLoading.show({
                        template: '<ion-spinner icon="circles" class="spinner-energized"></ion-spinner>',
                        duration: 60000
                    });
                }

                $http({
                    method: type,
                    url: url,
                    headers: {
                        'Content-Type': 'text/plain;charset=UTF-8',
                        'imei' : '862119036100407'
                    },
                    data: params
                }).success(function (data) {
                        $timeout(function () {
                            if (data && data.code == '00') {
                                defered.resolve(data.context);
                            } else {
                                defered.reject(data.message);
                            }
                        });
                    })
                    .error(function (error) {
                        $timeout(function () {
                            defered.reject(error);
                        });
                    })
                    .finally(function () {
                        $timeout(function () {
                            if (isShowLoading) {
                                $ionicLoading.hide();
                            }
                        });
                    });


                return defered.promise;
            }

            return {
                get: function (url, data, isShowLoading) {
                    return httpResource("GET", url, data, isShowLoading);
                },
                post: function (url, data, isShowLoading) {
                    return httpResource("POST", url, data, isShowLoading);
                },
                httpResource: httpResource
            };

        }])


    .factory("MXCommonService", ['$q', function ($q) {

        var currentUser = null;
        var serverUrl = null;

        function  getLocation() {
            var deferred = $q.defer();
            try{
                document.addEventListener("deviceready", function () {
                    MXLocation.getLocation(function(res){
                        deferred.resolve(res);
                    });

                },false)
            }catch (e){
                deferred.reject(e);
            }
            return deferred.promise;
        }

        function getCurrentUser() {

            var deferred = $q.defer();

            try {
                if (currentUser) {
                    deferred.resolve(currentUser);
                } else {
                    document.addEventListener("deviceready", function () {
                        MXCommon.getCurrentUser(
                            function (result) {
                                currentUser = result;
                                deferred.resolve(currentUser);
                            }, function () {
                                deferred.reject();
                            }
                        );

                    });
                }
            } catch (e) {
                deferred.resolve("error");
            }

            return deferred.promise;

        }

        function shareToChatAuto(_options) {

            var options = angular.extend({}, {
                'title': '', //分享标题
                'image_url': '', //缩略图url
                'url': '', //分享url
                'app_url': '', //app_url,原生的页面。如果是分享的html页面，该字段设置为空
                'description': '', //分享描述
                'source_id': '', //资源id,比如应用商店中的应用的id,或者公众号的id
                'source_type': '', // 值为ocu或app，资源类型
                'conversation_id': '' // 群聊的conversation_id
            }, _options);

            var deferred = $q.defer();

            try {
                document.addEventListener("deviceready", function () {
                    MXShare.shareToChatAuto(options
                        , function () {
                            deferred.resolve();
                        }, function () {
                            deferred.reject();
                        }
                    );
                });
            } catch (error) {
                deferred.reject();
            }

            return deferred.promise;
        }

        function getConversationByID(conversation_id) {

            var result = ["pengzhiyuan", "zhaojunming"];

            var deferred = $q.defer();

            try {
                document.addEventListener("deviceready", function () {
                    MXChat.getConversationByID(conversation_id,
                        function (result) {
                            deferred.resolve(JSON.parse(result));
                        }, function (error) {
                            deferred.reject("获取当前聊天人错误" + JSON.stringify(error));
                        });
                }, false);
            } catch (e) {
                deferred.reject(e.name + ":" + e.message);
            }

            return deferred.promise;
        }


        function getServerUrl() {

            var deferred = $q.defer();

            try {
                if (serverUrl) {
                    deferred.resolve(serverUrl);
                } else {
                    document.addEventListener("deviceready", function () {

                        MXCommon.getServerUrl(
                            function (url) {
                                serverUrl = url;
                                deferred.resolve(serverUrl);
                            }, function () {
                                deferred.reject();
                            }
                        );
                    });
                }
            } catch (e) {
                deferred.resolve("http://im.zsmarter.com");
            }

            return deferred.promise;
        }

        return {
            MXCommon: {
                getCurrentUser: getCurrentUser,
                getServerUrl: getServerUrl,
                getLocation:getLocation
            },
            MXShare: {
                shareToChatAuto: shareToChatAuto
            },
            MXChat: {
                getConversationByID: getConversationByID
            }
        }

    }]).filter('TimerMillisecondFormat', [function () {

    return function (timerMillisecond, format) {

        function day(time) {
            return parseInt(time / (1000 * 60 * 60 * 24));
        };
        function hour(time) {
            var result = parseInt((time / (1000 * 60 * 60)) % 24);
            return result > 9 ? result : '0' + result;
        };
        function minute(time) {
            var result = parseInt((time / (1000 * 60)) % 60);
            return result > 9 ? result : '0' + result;
        };
        function second(time) {
            var result = parseInt((time / 1000) % 60);
            return result > 9 ? result : '0' + result;
        };
        function milliSecond(time) {
            return time % 1000;
        };

        var countDownFormat = {
            day: 0,
            hour: 0,
            minute: 0,
            second: 0,
            milliSecond: 0
        };

        if ('d' === format || 'D' == format) {
            countDownFormat = day(timerMillisecond);
        } else if ('h' === format || 'H' == format) {
            countDownFormat = hour(timerMillisecond);
        }
        else if ('m' === format || 'M' == format) {
            countDownFormat = minute(timerMillisecond);
        }
        else if ('s' === format || 'S' == format) {
            countDownFormat = second(timerMillisecond);
        }
        else if ('ms' === format || 'MS' == format) {
            countDownFormat = milliSecond(timerMillisecond);
        }
        else {
            countDownFormat = {
                day: day(timerMillisecond),
                hour: hour(timerMillisecond),
                minute: minute(timerMillisecond),
                second: second(timerMillisecond),
                milliSecond: milliSecond(timerMillisecond)
            };
        }
        return countDownFormat;
    };

}]);
