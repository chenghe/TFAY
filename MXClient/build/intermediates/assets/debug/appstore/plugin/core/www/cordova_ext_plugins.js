cordova.define('cordova/plugin_ext_list', function (require, exports, module) {
        module.exports = [
            {
                "file": "plugins/com.minxing.client.plugin.web.location/www/mxlocation.js",
                "id": "com.minxing.client.plugin.web.location.MXLocation",
                "clobbers": [
                    "MXLocation"
                ]
            },

            {
                "file": "plugins/zsmarter-zsimei/www/zsmarterzsimei.js",
                "id": "com.minxing.client.plugin.web.zsmarter.zsimei.ZSImei",
                "clobbers": [
                    "ZSImei"
                ]
            },
            {
                "file": "plugins/zsmarter-cisco/www/zscisco.js",
                "id": "com.minxing.client.plugin.web.zsmarter.zscisco",
                "clobbers": [
                    "ZScisco"
                ]
            }

        ];
        module.exports.metadata =
            // TOP OF METADATA
            {}
        // BOTTOM OF METADATA
    });