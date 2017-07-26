cordova.define("com.minxing.client.plugin.web.zsmarter.zsimei.ZSImei",function(require, exports, module) {

var exec = require('cordova/exec');
var platform = require('cordova/platform');

module.exports={
            getImei:function(completeCallback){
            exec(completeCallback,null,"ZSImei","GetImei",[]);
            }
    }

//exports.getImei = function(success,error,params) {
//    // , ,Android de plugin class ,action,params
//    exec(success, error, "ZSImei","GetImei",[params]);
//};

});
