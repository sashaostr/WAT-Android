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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.smartbee.wat.Fragment.NoNetworkFragment;
import com.smartbee.wat.Helper.Constants;

import java.util.regex.Pattern;

public class WATModalActivity extends ActionBarActivity implements WATWebViewCallbacks {

    private PlaceholderFragment mPlaceholderFragment;
    private boolean mHideBackButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watmodal);
        mPlaceholderFragment = new PlaceholderFragment();
        mPlaceholderFragment.setArguments(getIntent().getExtras());

        this.mHideBackButton = getIntent().getBooleanExtra(Constants.MODAL_HIDE_BACK_BUTTON, false);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mPlaceholderFragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.watmodal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        /*
        if (id == R.id.action_settings) {
            return true;
        }
        */
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponseError(int errorCode, String description) {
        //todo: show 404 message for offline
        if(404 == errorCode
                || WebViewClient.ERROR_HOST_LOOKUP == errorCode
                || WebViewClient.ERROR_CONNECT == errorCode
                || WebViewClient.ERROR_TIMEOUT == errorCode ) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container,  new NoNetworkFragment())
                    .commit();
        }
    }

    @Override
    public void onModalFinished(int code, Intent data) {
        setResult(code, data);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (mPlaceholderFragment != null && mPlaceholderFragment.handleBackButton() && !mHideBackButton) {
                super.onBackPressed();
        } else if (!mHideBackButton) {
                super.onBackPressed();
        }

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private static WebView mWebView;
        private String mUrlToLoad;
        private String mPatternToMatchForClose;
        private Pattern mMatchForClosePattern;

        private WATWebViewCallbacks mCallbacks;
        private Context mContext;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_watmodal, container, false);

            mWebView = (WebView) rootView.findViewById(R.id.web_view);

//            if (savedInstanceState == null)
//            {
                configureWebView();
                this.mUrlToLoad = getArguments().getString(Constants.MODAL_ORIGINAL_URL);
                this.mPatternToMatchForClose = getArguments().getString(Constants.MODAL_CLOSE_ON_MATCH);
                this.mMatchForClosePattern = (Pattern) getArguments().get(Constants.MODAL_CLOSE_ON_MATCH_PATTERN);
                Object obj = getArguments().get(Constants.MODAL_CLOSE_ON_MATCH_PATTERN);
                configureWebView();
                loadURL(this.mUrlToLoad);
            //}

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            this.mCallbacks = (WATWebViewCallbacks)activity;
            this.mContext = activity;
        }

        @Override
        public void onDetach() {
            super.onDetach();
            this.mCallbacks = null;
            this.mContext = null;
        }

        private void configureWebView()
        {
            if (mWebView != null) {
                mWebView.getSettings().setJavaScriptEnabled(true);
                mWebView.getSettings().setBuiltInZoomControls(false);
                mWebView.getSettings().setLoadsImagesAutomatically(true);
                mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
                mWebView.setScrollbarFadingEnabled(true);
                mWebView.setWebViewClient(new WATModalWebViewClient(mContext, mCallbacks, mPatternToMatchForClose, mMatchForClosePattern));
            }
        }

        public static void loadURL(String url)
        {
            if (mWebView != null)
            {
                mWebView.loadUrl(url);
            }
            else
            {
                Log.e(Constants.TAG, "Error loading URL in modal webview");
            }
        }

        /**
         * Handles the back button being pressed.  While navigate back webpages if possible.
         * @return indicates if Activity back button activity should be performed by caller.
         */
        public boolean handleBackButton() {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
                return false;
            } else {
                return true;
            }
        }
    }
}
