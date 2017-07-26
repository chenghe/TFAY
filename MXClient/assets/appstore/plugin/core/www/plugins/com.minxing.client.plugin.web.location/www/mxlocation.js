cordova.define("com.minxing.client.plugin.web.location.MXLocation", function(require, exports, module) { /*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/

var exec = require('cordova/exec');
var platform = require('cordova/platform');

/**
 * Provides access to notifications on the device.
 */

module.exports = {

    /**
     * Open a native alert dialog, with a customizable title and button text.
     *
     * @param {String} message              Message to print in the body of the alert
     * @param {Function} completeCallback   The callback that is called when user clicks on a button.
     * @param {String} title                Title of the alert dialog (default: Alert)
     * @param {String} buttonLabel          Label of the close button (default: OK)
     */
    getSSOToken: function(ocuID, completeCallback) {
        exec(completeCallback, null, "MXLocation", "getSSOToken",  [ocuID]);
    },
	getCurrentUser: function(completeCallback) {
        exec(completeCallback, null, "MXLocation", "getCurrentUser",  []);
    },
	getServerUrl: function(completeCallback) {
        exec(completeCallback, null, "MXLocation", "getServerUrl",  []);
    },
	download: function(url) {
        exec(null, null, "MXLocation", "download",  [url]);
    },
	getPersonInfo: function(loginName, completeCallback) {
        exec(completeCallback, null, "MXLocation", "getPersonInfo",  [loginName]);
    },
	viewPersonInfo: function(loginName) {
        exec(null, null, "MXLocation", "viewPersonInfo",  [loginName]);
    },
    call: function(number) {
        exec(null, null, "MXLocation", "call",  [number]);
    },
	
	/**
     * 调用敏行RestAPI.
     *
     * @param {Json} params  
					 'method'          http method 如:get post
					 'url'             Rest API url
					 'params'          参数
					 'headers'         http header

     * @param {Function} completeCallback   方法回调

     */
	 
	api: function(content,success,error) {
	
	 var success = content.success||success,
        error = content.error||error,
        complete = content.complete;
        content.type=content.type||content.method;
    var successCallback = function() {
        if (success) {
            success.apply(content.context || this, arguments);
        }
        if (complete) {
            complete.apply(content.context || this, arguments);
        }
    }
    var errorCallback = function() {
        if (error) {
            error.apply(content.context || this, arguments);
        }
        if (complete) {
            complete.apply(content.context || this, arguments);
        }
    }
    exec(successCallback, errorCallback, "MXLocation", "api", [content]);
	
    },
	ajax: function(content) {
		   var success = content.success,
		   error = content.error,
		   complete = content.complete,
		   headers = content.headers = (content.headers || {});
		   var successCallback = function() {
		   if (success) {
		   success.apply(content.context || this, arguments);
		   }
		   if (complete) {
		   complete.apply(content.context || this, arguments);
		   }
		   }
		   var errorCallback = function() {
		   if (error) {
		   error.apply(content.context || this, arguments);
		   }
		   if (complete) {
		   complete.apply(content.context || this, arguments);
		   }
		   }
		   if (content.contentType) {
		   headers["Content-Type"] = content.contentType
		   }
		   if (content.dataType) {
		   var dataMimeType;
		   switch (content.dataType) {
		   case "xml":
		   dataMimeType = "application/xml, text/xml, */*";
		   break;
		   case "html":
		   dataMimeType = "text/html, */*";
		   break;
		   case "json":
		   dataMimeType = "application/json, text/javascript, */*";
		   break;
		   case "text":
		   dataMimeType = "text/plain, */*";
		   break;
		   default:
		   dataMimeType = "*/*";
		   break;
		   }
		   headers["Accept"] = dataMimeType;
		   
		   }
		   exec(successCallback, errorCallback, "MXLocation", "ajax", [content]);	
    },
	loginWithToken: function(access_token, token_type, expires_in, refresh_token) {
        exec(null, null, "MXLocation", "loginWithToken",  [access_token, token_type, expires_in, refresh_token]);
    },
	getWaterMarkUrl: function(completeCallback) {
        exec(completeCallback, null, "MXLocation", "getWaterMarkUrl",  []);
    },
    getLocation: function(completeCallback){
    	exec(completeCallback,null,"MXLocation","getLocation",[]);
    },
	
	
};

});


