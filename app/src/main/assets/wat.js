/*
Web App Template for Android 

Copyright (c) Microsoft Corporation

All rights reserved. 

MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the ""Software""), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED *AS IS*, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

*/

(function() {
    var wat = {
        addStyle : function (styleText) {
            var style = document.createElement('style');
            style.innerHTML = styleText;
            document.head.appendChild(style);
        },
        addStyleEncoded : function (styleEncoded) {
            var style = document.createElement('style');
            style.innerHTML = window.atob(styleEncoded);
            document.head.appendChild(style);
        },
        addStyleLink : function (styleUrl) {
            var link = document.createElement('link');
            link.setAttribute('rel', 'stylesheet');
            link.type = 'text/css';
            link.href = styleUrl;
            document.head.appendChild(link);
        },
        addScript: function (scriptText) {
            var script = document.createElement('script');
            script.type = 'text/javascript';
            script.innerHTML = scriptText;
            document.body.appendChild(script);
        },
        addScriptHref: function(scriptUrl) {
            var script = document.createElement('script');
            script.href = scriptUrl;
            script.type = 'text/javascript';
            document.body.appendChild(script);
        },
        addScriptEncoded: function(encoded) {
             var script = document.createElement('script');
             script.type = 'text/javascript';
             script.innerHTML = window.atob(encoded);
             document.body.appendChild(script);
         }
    };

    window.msWebApplicationToolkit = wat;
})();