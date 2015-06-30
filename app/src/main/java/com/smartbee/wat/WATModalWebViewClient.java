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
import android.content.Intent;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.smartbee.wat.Helper.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class WATModalWebViewClient extends WebViewClient {

    WATWebViewCallbacks mListener;
    Context mContext;
    String mPatternToMatchForClose;
    Pattern mMatchForClosePattern;

    public WATModalWebViewClient(Context context, WATWebViewCallbacks listener, String patterToMatchForClose, Pattern matchForClosePattern) {
        if (context == null) {
            throw new IllegalArgumentException("context is a required parameter");
        }

        this.mListener = listener;
        this.mContext = context;
        this.mPatternToMatchForClose = patterToMatchForClose;
        this.mMatchForClosePattern = matchForClosePattern;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

        Log.i(Constants.TAG, "URL fromModal: " + url);
        Matcher matcher = mMatchForClosePattern.matcher(url);
        if (matcher.find()) {
            //Trigger activity close
            Intent intent = new Intent();
            intent.putExtra(Constants.MODAL_RESPONSE_URL, url);
            mListener.onModalFinished(Constants.MODAL_RESPONSE_CODE, intent);
            return true;
        }
        return super.shouldOverrideUrlLoading(view, url);
    }
}
