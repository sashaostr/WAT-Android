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

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smartbee.wat.Helper.Assets;
import com.smartbee.wat.Helper.Constants;
import com.smartbee.wat.Model.Config;
import com.smartbee.wat.Model.ConfigCustomScript;
import com.smartbee.wat.Model.ConfigRedirectRules;
import com.smartbee.wat.Model.ConfigStyles;
import com.smartbee.wat.Model.UrlHistory;

import org.apache.http.util.EncodingUtils;

import java.util.Hashtable;
import java.util.regex.Matcher;

/**
 * WATWebViewClient receives events from the WebView
 * and handles injection of Css/Javascript
 */
public class WATWebViewClient extends WebViewClient {
    private static String watJs;

    WATWebViewCallbacks mListener;
    Context mContext;
    ConfigRedirectRules[] mRules;
    Hashtable<String, UrlHistory> mVistedUrls;

    /**
     * Helper function to load the WAT javascript code used to inject custom javascript and css
     * @return
     */
    private String getWatJs() {
        if (watJs == null) {
            watJs = Assets.readText("wat.min.js", mContext);
        }

        return watJs;
    }

    public WATWebViewClient(Context context, Config config, WATWebViewCallbacks listener) {
        
        if (config == null) {
            throw new IllegalArgumentException("config is a required parameter");
        }
        if (context == null) {
            throw new IllegalArgumentException("context is a required parameter");
        }

        this.mListener = listener;
        this.mContext = context;
        if (config != null && config.getRedirects() != null)
            this.mRules = config.getRedirects().getRules();
        mVistedUrls = new Hashtable<String, UrlHistory>();
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.i(Constants.TAG, "shouldOverrideUrlLoading: " + url);
        //return super.shouldOverrideUrlLoading(view, url);
        return this.handleUrlRedirection(view, url);
        //return false;
    }

    private boolean handleUrlRedirection(WebView webView, String url) {

        Config config = ((WATApp) ((Activity)mContext).getApplication()).getConfig();
        if (config.getRedirects()!=null && config.getRedirects().isEnabled() && mRules.length > 0) {
            //Check for already handled
            UrlHistory urlHistory = mVistedUrls.get(url);
            if (urlHistory != null && !urlHistory.getDidTriggerRedirect()) {
                Log.i(Constants.TAG, "Bypassing redirect check");
                return false;
            } else {
                urlHistory = new UrlHistory(url);
            }

            boolean redirectHandled = false;
            for (int i = 0; i < mRules.length; i++) {
                ConfigRedirectRules rule = mRules[i];
                Matcher matcher = rule.getRegexPattern().matcher(url);
                if (matcher.find()) {
                    switch (rule.getActionType()) {
                        case showMessage:
                            Toast toast = Toast.makeText(mContext, rule.getMessage(), Toast.LENGTH_LONG);
                            LinearLayout layout = (LinearLayout) toast.getView();
                            if (layout.getChildCount() > 0) {
                                TextView textView = (TextView) layout.getChildAt(0);
                                textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                            }
                            toast.show();
                            redirectHandled = true;
                            break;
                        case popout:
                            launchUrlInBrowser(url);
                            redirectHandled = true;
                            break;
                        case redirect:
                            String redirectUrl = rule.getUrl();
                            if (config.getRedirects().isEnableCaptureWindowOpen())
                                webView.loadUrl(redirectUrl);
                            else {
                                launchUrlInBrowser(url);
                            }
                            redirectHandled = true;
                            break;
                        case modal:
                            Intent intent = new Intent(mContext, WATModalActivity.class);
                            intent.putExtra(Constants.MODAL_ORIGINAL_URL, url);
                            intent.putExtra(Constants.MODAL_CLOSE_ON_MATCH, rule.getCloseOnMatch());
                            intent.putExtra(Constants.MODAL_CLOSE_ON_MATCH_PATTERN, rule.getCloseOnMatchPattern());
                            intent.putExtra(Constants.MODAL_HIDE_BACK_BUTTON, rule.isHideCloseButton());
                            ((Activity) mContext).startActivityForResult(intent, Constants.MODAL_REQUEST_CODE);
                            redirectHandled = true;
                            break;
                        case unknown:
                            Log.w(Constants.TAG, "Unspecified rule action type: " + rule.getAction());
                            break;
                    }
                }
            }
            //Store history of this URL being visited
            if (redirectHandled)
                urlHistory.setDidTriggerRedirect(true);
            mVistedUrls.put(url, urlHistory);
            return redirectHandled;
        } else {
            return false;
        }
    }

    private void launchUrlInBrowser(String url) {
        Intent webIntent = new Intent(Intent.ACTION_VIEW);
        webIntent.setData(Uri.parse(url));
        mContext.startActivity(webIntent);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Log.i(Constants.TAG, "onPageStarted: " + url);

        /*
        if (!this.handleUrlRedirection(view, url)) {
            super.onPageStarted(view, url, favicon);
        } else {
            view.stopLoading();
        }
        */
    }

    /**
     * Traps errors from the webview and forwards them to the listener.
     *
     * @param view
     * @param errorCode
     * @param description
     * @param failingUrl
     */
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);

        if (this.mListener != null) {
            this.mListener.onResponseError(errorCode, description);
        }
    }

    private String lastLoadedUrl = "";
    /**
     * Called when a web page has completed loading in the webview.
     *
     * @param view
     * @param url
     */
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        // This code should solve a problem where hashed links still gose into the onPage
        // Finish, and the recall to the inject script cause stack overflow.
        boolean shouldLoadScripts = true;
        if (!lastLoadedUrl.equals("")) {

            String notHushedLastUrl =lastLoadedUrl;
            int pos = lastLoadedUrl.indexOf('#');
            if (pos > -1){
                notHushedLastUrl= lastLoadedUrl.substring(0, pos);
            }
            String notHushedUrl = url;
            pos = url.indexOf('#');
            if (pos > -1){
                notHushedUrl= url.substring(0, pos);
            }

            if (notHushedLastUrl.equals(notHushedUrl)){
                shouldLoadScripts = false;
            }
        }

        lastLoadedUrl = url;
        if (shouldLoadScripts) {
            injectScriptAndStyles(view);
        }
    }

    /**
     * Injects javascript and css into the webview to help customize the web page to work
     * better in the app.  The javascript and css rules to be injected are configured in the
     * '/assets/config.json' file.
     *
     * @param view
     */
    private void injectScriptAndStyles(WebView view) {
        //wrap everything in a self executing anonymous function
        StringBuilder jsBuilder = new StringBuilder("javascript:(function(){ ");

        //load up a few js helper functions first
        jsBuilder.append(getWatJs());

        Config config = ((WATApp) ((Activity)mContext).getApplication()).getConfig();
        //add all the custom script files by inlining them into the html using helper functions
        ConfigCustomScript customScript = config.getCustomScript();
        if (customScript !=  null) {
            String scripts[] = customScript.getScriptFiles();
            if (scripts != null) {
                for (int i = 0; i < scripts.length; i++) {
                    String encodedJs = Assets.readEncoded(scripts[i], mContext);
                    if (encodedJs != null) {
                        jsBuilder.append("window.msWebApplicationToolkit.addScriptEncoded('" + encodedJs + "'); ");
                    }
                }
            }
        }

        //add all the custom CSS by inlining them into the html using helper functions
        String encodedStyles = getStylesEncoded();
        if (encodedStyles != null && encodedStyles.length()> 0) {
            jsBuilder.append("window.msWebApplicationToolkit.addStyleEncoded('" + encodedStyles + "');");
        }

        //close our self executing function
        jsBuilder.append("})();");


        //if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        //loadJavascript(view, jsBuilder.toString());
        //}
        //else {

        //execute the javascript in the webview by navigating to a 'javascript:' url
        view.loadUrl(jsBuilder.toString());

        //}
    }

    /**
     * Combines styles from
     * (1) the "hiddenElements" node in config.json
     * (2) the "customCssFile"
     * (3) the "customCssRules"
     *
     * @return the combined css rules.
     */
    private String getStyles() {
        Config config = ((WATApp) ((Activity)mContext).getApplication()).getConfig();
        //create a rule for all css selectors that should be hidden
        StringBuilder styleBuilder = new StringBuilder();
        ConfigStyles styles = config.getStyles();
        if (styles != null) {
            String[] hiddenElements = styles.getHiddenElements();
            if (null != hiddenElements && hiddenElements.length > 0) {
                styleBuilder.append(TextUtils.join(", ", hiddenElements) + " { display: none; }");
            }

            //read all the css rules from the custom css file in the assets folder
            String cssFile = styles.getCustomCssFile();
            if (cssFile != null) {
                String cssFileContents = Assets.readText(cssFile, mContext);
                if (cssFileContents != null) styleBuilder.append(cssFileContents);
            }

            //read the css rules from the config.json file
            String cssText = styles.getCustomCssString();
            if(cssText != null) {
                styleBuilder.append(cssText);
            }
        }

        //all the style rules from the 3 different sources are now concatenated together
        return styleBuilder.toString();
    }

    /**
     * Returns the css styles to be injected as a base64 encoded string.  Base64 encoding
     * helps to inject the CSS using the loadUrl('javascript:' ...) technique.
     * @return all css rules, base64 encoded
     */
    private String getStylesEncoded() {
        String plainText = getStyles();
        return Base64.encodeToString(EncodingUtils.getBytes(plainText, "UTF-8"), Base64.NO_WRAP);
    }

    /**
     * Alternate method for loading javascript into a webview on API level 19 and above.  Not
     * currently used.
     * @param view
     * @param javascript
     */
    @TargetApi(19)
    private void loadJavascript(WebView view, String javascript) {
        view.evaluateJavascript(javascript, new ValueCallback<String>(){
            @Override
            public void onReceiveValue(String s) {

            }
        });
    }
}