cordova.define("com.minxing.client.plugin.web.zsmarter.zscisco",function(require, exports, module) {

var exec = require('cordova/exec');
var platform = require('cordova/platform');

module.exports={
            startCisco:function(completeCallback,error,param){
            exec(completeCallback,error,"ZScisco","startCisco",[param]);
            }
    }


});
