/*
Web App Template for Android 

Copyright (c) Microsoft Corporation

All rights reserved. 

MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the ""Software""), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED *AS IS*, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

*/

package com.smartbee.wat;

import android.content.Context;
import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;

import com.smartbee.wat.Model.Config;

/**
 * Helper method to provide WebChrome functionality to the WebView.  Can be used to implement
 * console.log, and alert/messagebox functionality.
 */
public class WATWebChromeClient extends WebChromeClient {
    Context mContext;

    public WATWebChromeClient(Context context, Config config)
    {
        if (config == null) {
            throw new IllegalArgumentException("config is a required parameter");
        }
        if (context == null) {
            throw new IllegalArgumentException("context is a required parameter");
        }

        this.mContext = context;
    }


    @SuppressWarnings("unused")
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType, String capture) {
        this.openFileChooser(uploadMsg);
    }

    @SuppressWarnings("unused")
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType) {
        this.openFileChooser(uploadMsg);
    }

    public void openFileChooser(ValueCallback<Uri> uploadMsg)
    {

//        mUploadMessage = uploadMsg;
//        PesScreen.this.openImageIntent();
    }

}
