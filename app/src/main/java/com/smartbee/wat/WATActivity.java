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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.smartbee.wat.Fragment.NavigationDrawerFragment;
import com.smartbee.wat.Fragment.NoNetworkFragment;
import com.smartbee.wat.Fragment.SettingsFragment;
import com.smartbee.wat.Helper.Constants;
import com.smartbee.wat.Helper.Net;
import com.smartbee.wat.Model.Config;
import com.smartbee.wat.Model.ConfigAppBarButtons;
import com.smartbee.wat.Model.ConfigNavBarButtons;
import com.smartbee.wat.Model.ConfigNavBarChildButtons;
import com.smartbee.wat.Model.ConfigShare;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WATActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, WATWebViewCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private PlaceholderFragment mPlaceholderFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private ProgressDialog mProgressDialog;

    private final static int sActionMenuItemRootId = 100;

    private ShareActionProvider mShareActionProvider;
    private int mShareItemId = 99;
    private int mMenuChangeConfigId = 999;
    private int mMenuResetStoredConfigId = 998;

    private Context mContext;

//    @Override
//    public java.io.FileInputStream openFileInput(java.lang.String s) throws java.io.FileNotFoundException
//    {
//        return null;
//    }

//    @Override
//    public java.io.FileInputStream openFileInput(java.lang.String s) throws java.io.FileNotFoundException
//    {
//        return null;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wat);
        mContext = this;
        mTitle = getTitle();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer (if navigation items required)
        Config config = ((WATApp) getApplication()).getConfig();
        if ( config.getNavBar() != null ) {
            if ( config.getNavBar().getButtons() != null ) {
                mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
            }
        }

        // Warn if internet connection required, but not available...
        if ( ! Net.isNetworkAvailable(this) )
        {
            createAndShowDialog("Could not connect.", "Network Error");
        }
    }

    private String getHomeURL()
    {
        Config config = ((WATApp) getApplication()).getConfig();
        String url = "";
        if( config != null )
        {
            url = config.getHomeURL();
        }
        return url;
    }

    private void doNavigation(int groupPosition, int childPosition) {
        String action = getHomeURL(); // default
        Config config = ((WATApp) getApplication()).getConfig();
        if( config != null ) {
            ConfigNavBarButtons[] btns = config.getNavBar().getButtons(); // virtual
            ArrayList<ConfigNavBarButtons> visibleBtns = new ArrayList<ConfigNavBarButtons>(); // actual
            for ( ConfigNavBarButtons btn : btns ) {
                // reflects what items are allowed in navigation drawer
                //if ( ! btn.getAction().equalsIgnoreCase("back") && ! btn.getAction().equalsIgnoreCase("eval") ) {
                    visibleBtns.add(btn);
                //}
            }
            ConfigNavBarButtons sectionButton = visibleBtns.get(groupPosition);
            if(sectionButton != null) {
                mTitle = sectionButton.getLabel();
                if ( sectionButton.getAction().equalsIgnoreCase("home") )
                {
                    action = getHomeURL();
                    Log.i("HOME", String.format("Goto URL: %s", action) );
                    PlaceholderFragment.loadURL(action);
                } else if ( sectionButton.getAction().equalsIgnoreCase("nested") ) {
                    ConfigNavBarChildButtons[] children = sectionButton.getChildren();
                    ConfigNavBarChildButtons childButton = children[childPosition];
                    action = childButton.getAction();
                    mTitle = String.format("%s: %s", sectionButton.getLabel(), childButton.getLabel());
                    Log.i("NESTED", String.format("Goto URL: %s", action) );
                    PlaceholderFragment.loadURL(action);
                } else if ( sectionButton.getAction().equalsIgnoreCase("eval") ) {
                    Log.i("EVAL", String.format("JS: %s", action) );
                    PlaceholderFragment.evalJavascript(sectionButton.getData());
                } else if ( sectionButton.getAction().equalsIgnoreCase("back") ) {
                    if (mPlaceholderFragment != null) {
                        Log.i("BACK", String.format("Goto URL: %s", action) );
                        mPlaceholderFragment.handleBackButton();
                    } else {
                        //onBackPressed(); // exits app
                    }
                } else {
                    action = sectionButton.getAction();
                    Log.i("SECTION", String.format("Goto URL: %s", action) );
                    PlaceholderFragment.loadURL(action);
                }
            }
        }

    }

    // Common methods

    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }
    private void createAndShowDialog(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }



    @Override
    public void onNavigationDrawerItemSelected(int groupPosition, int childPosition) {
        // update the main content by replacing fragments
        Config config = ((WATApp) getApplication()).getConfig();
        if ((mPlaceholderFragment == null || config.getHasConfigChanged()) && config != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            String start_url = getHomeURL();
            mPlaceholderFragment = PlaceholderFragment.newInstance(groupPosition + 1, start_url);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, mPlaceholderFragment)
                    .commit();
            Log.i("FRAG", String.format("Creating new fragment instance with homepage: %s", start_url) );
            config.markConfigUsed();
        } else {
            doNavigation(groupPosition, childPosition); // handles URL and special cases: 'back' & 'eval'
        }
    }

    public void onSectionAttached(int number) {
        Log.i("ATTACH", String.format("Section attached #%d", number)); // this is just called the first time the fragment is attached
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    private void attachAppBarButtonsToActionBarMenu(Menu menu) {
        int groupId = 0;
        int orderId = 1;
        int itemId = sActionMenuItemRootId;
        Config config = ((WATApp) getApplication()).getConfig();
        if( config != null ) {
            if ( config.getAppBar()!=null && config.getAppBar().isEnabled() ) {
                //Clear menu to remove any old entities
                menu.clear();
                for(ConfigAppBarButtons btn : config.getAppBar().getButtons() ) {
                    menu.add(groupId,itemId,orderId, btn.getLabel() );
                    MenuItem menuItem = menu.findItem(itemId);
                    if (Build.VERSION.SDK_INT > 10) {
                        int resId = getResources().getIdentifier("ic_action_"+btn.getIcon(),"drawable",getPackageName()) |
                                getResources().getIdentifier(btn.getIcon(),"drawable",getPackageName()) ;
                        if(resId>0) {
                            menuItem.setIcon(resId); // R.drawable.ic_action_about
                            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT); // setShowAsAction requires API level 11
                        }
                    }
                    orderId++;
                    itemId++;
                }
            }
            if ( config.getShare()!=null && config.getShare().isEnabled() ) {
                appendShareActionsToActionBarMenu(menu, groupId);
            }

            if ( Constants.ALLOW_REMOTE_CONFIG_CHANGE) {
                appendChangeRemoteConfigActionToActionBarMenu(menu, groupId);
            }
        }
    }

    private void appendChangeRemoteConfigActionToActionBarMenu(Menu menu, int groupId) {
        menu.add(groupId, mMenuChangeConfigId, mMenuChangeConfigId, getResources().getString(R.string.change_config_url));
        menu.add(groupId, mMenuResetStoredConfigId, mMenuResetStoredConfigId, getResources().getString(R.string.reset_config_url));
    }

    private void appendShareActionsToActionBarMenu(Menu menu, int groupId) {
        // Easy Share Action requires API Level 14
        if (Build.VERSION.SDK_INT > 13) {
            Config config = ((WATApp) getApplication()).getConfig();
            ConfigShare share = config.getShare();
            ConfigAppBarButtons btn = new ConfigAppBarButtons("Share", "share", "share", share.getMessage() ); // label,icon,action,data
            menu.add(groupId,mShareItemId,mShareItemId, btn.getLabel() );
            MenuItem menuItem = menu.findItem(mShareItemId);
            mShareActionProvider = (ShareActionProvider) menuItem.getActionProvider();
        }
    }

    // Share intent TODO: would need to implement special cases for sharing across various social media, at the moment it's the lowest common denominator - URL only.
    public Intent doShare() {
        // share text
        Config config = ((WATApp) getApplication()).getConfig();
        String shareTitle = config.getShare().getTitle();
        String shareMessage = config.getShare().getMessage();
        String shareURL = config.getShare().getUrl();

        // share link
        if( shareURL.equalsIgnoreCase("{currentURL}")) {
            if (mPlaceholderFragment != null) {
                if( ! PlaceholderFragment.sCurrentUrl.equalsIgnoreCase("") ) {
                    shareURL = PlaceholderFragment.sCurrentUrl;
                } else {
                    shareURL = getHomeURL();
                }
            } else {
                Log.e("FRAG","Fragment view not ready.");
            }
        }

        // share image
        //Uri path = Uri.parse(String.format("android.resource://%s/%s", this.getPackageName(), getResources().getDrawable(R.drawable.ic_launcher) ));

        // sharing intent
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain"); // "text/plain" "text/html" "image/*" "*/*"
        //intent.putExtra(Intent.EXTRA_SUBJECT, shareTitle );
        //intent.putExtra(Intent.EXTRA_TITLE, shareTitle );
        intent.putExtra(Intent.EXTRA_TEXT, shareURL); // shareMessage // only a link is supported by FaceBook! (See bug report: https://developers.facebook.com/x/bugs/332619626816423/ and http://stackoverflow.com/questions/13286358/sharing-to-facebook-twitter-via-share-intent-android)
        startActivity(Intent.createChooser(intent, getString(R.string.app_share))); // Share via
        return intent;
    }

    private void doChangeConfig() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.change_config_url_window_title));
        builder.setMessage(getResources().getString(R.string.change_config_url_window_message));
        //Add EditText
        final EditText txtConfigUrl = new EditText(this);
        txtConfigUrl.setSingleLine();
        //TODO: Remove this after testing
        //txtConfigUrl.setText("https://crcomcstorage.blob.core.windows.net/test/config2.js");
        builder.setView(txtConfigUrl);

        builder.setPositiveButton(getResources().getString(R.string.load_config), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showProgressIndicator();
                String configUrl = txtConfigUrl.getText().toString();
                ((WATApp) getApplication()).loadRemoteConfigWithUrl(configUrl);
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Cancelled, auto dismissed
            }
        });

        builder.show();
    }

    public void showProgressIndicator() {
        mProgressDialog = ProgressDialog.show(mContext, getString(R.string.loading), getString(R.string.loading_config), true, false);
        mProgressDialog.show();
    }

    public void endProgressIndicator() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.wat, menu);
            attachAppBarButtonsToActionBarMenu(menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    private Menu mMenu;
    public void setupActionBarOnRemoteLoad() {
        getMenuInflater().inflate(R.menu.wat, mMenu);
        attachAppBarButtonsToActionBarMenu(mMenu);
        restoreActionBar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Config config = ((WATApp) getApplication()).getConfig();
        if (config != null) {
            if (id == mShareItemId) {
                doShare();
            } else if (id == mMenuChangeConfigId) {
                doChangeConfig();
            } else if (id == mMenuResetStoredConfigId) {
                showProgressIndicator();
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = sp.edit();
                editor.remove(Constants.SHARED_PREF_CONFIG_URL);
                editor.commit();
                ((WATApp) getApplication()).getConfig(true);
                if (((WATApp) getApplication()).getConfig(true) != null) {
                    //Local config loaded, refresh
                    mNavigationDrawerFragment.handleConfigReload();
                }
            }else {
                if(config.getAppBar()!=null) {
                    ConfigAppBarButtons[] buttons = config.getAppBar().getButtons();
                    for (int i = 0; i < buttons.length; i++) {
                        if (id == i + sActionMenuItemRootId) {
                            ConfigAppBarButtons btn = buttons[i];
                            doMenuAction(btn);
                            break;
                        }
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void doMenuAction(ConfigAppBarButtons btn) {
        if( btn.getAction().equalsIgnoreCase("eval") ) {
            Toast.makeText(this, btn.getLabel(), Toast.LENGTH_SHORT).show();
            // eval javascript
            if (mPlaceholderFragment != null) {
                PlaceholderFragment.evalJavascript(btn.getData());
            } else {
                Log.e("FRAG","Fragment view not ready.");
            }
        } else if( btn.getAction().equalsIgnoreCase("settings") ) {
            //Toast.makeText(this, "Show settings", Toast.LENGTH_SHORT).show();
            Bundle args = new Bundle();
            args.putString("title", btn.getLabel());
            DialogFragment dialogFragment = new SettingsFragment();
            dialogFragment.setArguments(args);
            dialogFragment.show(getSupportFragmentManager(), btn.getLabel() );
        } else {
            // handle URL
            if (mPlaceholderFragment != null) {
                PlaceholderFragment.loadURL(btn.getAction());
            } else {
                Log.e("FRAG","Fragment view not ready.");
            }
        }
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
        Log.e(Constants.TAG, "onModalFinished not implemented");
    }

    @Override
    public void onBackPressed() {
        if (mPlaceholderFragment != null) {
            if (mPlaceholderFragment.handleBackButton()) {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == Constants.MODAL_REQUEST_CODE) {
//            Log.i(Constants.TAG, "onActivityResult");
//            Config config = ((WATApp) getApplication()).getConfig();
//            if (config.getRedirects().isRefreshOnModalClose()) {
//                mPlaceholderFragment.refreshWebView();
//                String responseUrl = data.getStringExtra(Constants.MODAL_RESPONSE_URL);
//                Log.i(Constants.TAG, "Response URL from modal: " + responseUrl);
//            }
//        }
//    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {

        //NOT LOLLIPOP:
        if(requestCode == this.mPlaceholderFragment.FILECHOOSER_RESULTCODE){
            if (null == this.mPlaceholderFragment.mUploadMessage)
                return;

            Uri result = null;
            if(resultCode == Activity.RESULT_OK) {
                if(data == null) {
                    // If there is not data, then we may have taken a photo
                    if(this.mPlaceholderFragment.imageUri != null) {
                        result = this.mPlaceholderFragment.imageUri;
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        result = Uri.parse(dataString);
                    }
                }
            }

//            Uri result = data == null || resultCode != RESULT_OK ? this.mPlaceholderFragment.imageUri : data.getData();
            this.mPlaceholderFragment.mUploadMessage.onReceiveValue(result);
            this.mPlaceholderFragment.mUploadMessage = null;
            return;
        }

        //LOLLIPOP:
        if(requestCode != this.mPlaceholderFragment.INPUT_FILE_REQUEST_CODE || this.mPlaceholderFragment.mFilePathCallback == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        Uri[] results = null;

        // Check that the response is a good one
        if(resultCode == Activity.RESULT_OK) {
            if(data == null) {
                // If there is not data, then we may have taken a photo
                if(this.mPlaceholderFragment.mCameraPhotoPath != null) {
                    results = new Uri[]{Uri.parse(this.mPlaceholderFragment.mCameraPhotoPath)};
                }
            } else {
                String dataString = data.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
            }
        }

        this.mPlaceholderFragment.mFilePathCallback.onReceiveValue(results);
        this.mPlaceholderFragment.mFilePathCallback = null;
        return;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_SECTION_URL = "section_url";

        private static WebView mWebView;

        private WATWebViewCallbacks mCallbacks;
        private Context mContext;

        public static String sCurrentUrl;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, String sectionURL) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(ARG_SECTION_URL, sectionURL);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

//        @Override
//        public void onStart(){
//            super.onStart();
//            if (shouldStartUrl) {
//                String url = getArguments().getString(ARG_SECTION_URL);
//                // NB: The webview maybe destroyed & recreated on orientation so URL is still reloaded here...
//                loadURL(url);
//            }
//        }

        private boolean shouldStartUrl = false;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_wat, container, false);
            mWebView = (WebView) rootView.findViewById(R.id.web_view);
            mWebView.getSettings().setDomStorageEnabled(true);

            sCurrentUrl="";
            configureWebView();
            if (savedInstanceState == null) {
                shouldStartUrl = true;
                String url = getArguments().getString(ARG_SECTION_URL);
                // NB: The webview maybe destroyed & recreated on orientation so URL is still reloaded here...
                loadURL(url);
            }
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            ((WATActivity) activity).onSectionAttached(sectionNumber);
            this.mCallbacks = (WATWebViewCallbacks)activity;
            this.mContext = activity;
        }

        @Override
        public void onDetach() {
            super.onDetach();
            this.mCallbacks = null;
            this.mContext = null;
        }

////        @Override
//        //NOT LOLLIPOP
//        public void onActivityResult2(int requestCode, int resultCode,
//                                        Intent intent) {
//            if(requestCode==FILECHOOSER_RESULTCODE)
//            {
//                if (null == mUploadMessage) return;
//                Uri result = intent == null || resultCode != RESULT_OK ? null
//                        : intent.getData();
//                mUploadMessage.onReceiveValue(result);
//                mUploadMessage = null;
//            }
//        }
//
//



        final static int CAPTURE_RESULTCODE = 2;
        String filePath;
        String mCameraPhotoPath;
        final static int FILECHOOSER_RESULTCODE=2;
        public static final int INPUT_FILE_REQUEST_CODE = 1;
        private static final String TAG = PlaceholderFragment.class.getSimpleName();
        ValueCallback<Uri> mUploadMessage;
        ValueCallback<Uri[]> mFilePathCallback;
        Uri imageUri;
        private void configureWebView()
        {
            if (mWebView != null) {
                mWebView.getSettings().setJavaScriptEnabled(true);
                mWebView.getSettings().setBuiltInZoomControls(false);
                mWebView.getSettings().setLoadsImagesAutomatically(true);
                mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
                mWebView.setScrollbarFadingEnabled(true);
                Config config = ((WATApp) getActivity().getApplication()).getConfig();
                if(mContext != null && config != null && mCallbacks != null) {
                    mWebView.setWebViewClient(new WATWebViewClient(mContext, config, mCallbacks));
//                    mWebView.setWebChromeClient(new WATWebChromeClient(mContext, config)
//                    {
//
//                        // openFileChooser for Android 3.0+
//                        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType){
//                            /**updated, out of the IF **/
//                            mUploadMessage = uploadMsg;
//                            /**updated, out of the IF **/
//
//
//
//                            try{
//                                File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "AndroidExampleFolder");
//                                if (!imageStorageDir.exists()) {
//                                    imageStorageDir.mkdirs();
//                                }
//                                File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
//                                mCapturedImageURI = Uri.fromFile(file); // save to the private variable
//
//                                final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
//                                // captureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//
//                                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//                                i.addCategory(Intent.CATEGORY_OPENABLE);
//                                i.setType("image/*");
//
//                                Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
//                                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[] { captureIntent });
//
//                                startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
//                            }
//                            catch(Exception e){
//                                //Toast.makeText(getBaseContext(), "Camera Exception:"+e, Toast.LENGTH_LONG).show();
//                            }
//                            //}
//                        }
//
//                        // openFileChooser for Android < 3.0
//                        public void openFileChooser(ValueCallback<Uri> uploadMsg){
//                            openFileChooser(uploadMsg, "");
//                        }
//
//                        //openFileChooser for other Android versions
//                        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
//                            openFileChooser(uploadMsg, acceptType);
//                        }
//
//                        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//                        @Override
//                        public boolean onShowFileChooser(WebView webView, final ValueCallback<Uri[]> filePathsCallback, final WebChromeClient.FileChooserParams fileChooserParams) {
//                            Intent intent = fileChooserParams.createIntent();
//                            try {
////                                parentEngine.cordova.startActivityForResult(new CordovaPlugin() {
////                                    @Override
////                                    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
////                                        Uri[] result = WebChromeClient.FileChooserParams.parseResult(resultCode, intent);
////                                        Log.d(LOG_TAG, "Receive file chooser URL: " + result);
////                                        filePathsCallback.onReceiveValue(result);
////                                    }
////                                }, intent, FILECHOOSER_RESULTCODE);
//                            } catch (ActivityNotFoundException e) {
//                                //Log.w("No activity found to handle file chooser intent.", e);
//                                filePathsCallback.onReceiveValue(null);
//                            }
//                            return true;
//                        }
//
////                        //For Android 4.1
////                        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
////                            mUploadMessage = uploadMsg;
////                            //Intent i = new Intent(Intent.ACTION_GET_CONTENT);
////                            Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
////                            i.addCategory(Intent.CATEGORY_OPENABLE);
////                            i.setType("image/*");
////                            PlaceholderFragment.this.startActivityForResult( Intent.createChooser( i, "File Chooser" ), PlaceholderFragment.FILECHOOSER_RESULTCODE );
////                        }
//
//
//
//                    });
//                    mWebView.setWebChromeClient(new WebChromeClient(mContext, config)
                    mWebView.setWebChromeClient(new WATWebChromeClient(mContext, config)
                    {

                        //The undocumented magic method override
                        //Eclipse will swear at you if you try to put @Override here
                        // For Android 3.0+
                        @SuppressWarnings("unused")
                        public void openFileChooser(ValueCallback<Uri> uploadMsg) {

                            mUploadMessage = uploadMsg;
                            File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyApp");
                            // Create the storage directory if it does not exist
                            if (! imageStorageDir.exists()){
                                imageStorageDir.mkdirs();
                            }
                            File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                            imageUri = Uri.fromFile(file);

                            final List<Intent> cameraIntents = new ArrayList<Intent>();
                            final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            final PackageManager packageManager = mContext.getPackageManager();
                            final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
                            for(ResolveInfo res : listCam) {
                                final String packageName = res.activityInfo.packageName;
                                final Intent i = new Intent(captureIntent);
                                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                                i.setPackage(packageName);
                                i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                cameraIntents.add(i);

                            }


                            mUploadMessage = uploadMsg;
                            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                            i.addCategory(Intent.CATEGORY_OPENABLE);
                            i.setType("image/*");
                            Intent chooserIntent = Intent.createChooser(i,"Image Chooser");
                            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
//                            PlaceholderFragment.this.startActivityForResult(chooserIntent,  FILECHOOSER_RESULTCODE);
                            getActivity().startActivityForResult(chooserIntent,  FILECHOOSER_RESULTCODE);

                        }

                        //For Android 4.1
                        @SuppressWarnings("unused")
                        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType){
                            mUploadMessage = uploadMsg;
                            File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyApp");
                            // Create the storage directory if it does not exist
                            if (! imageStorageDir.exists()){
                                imageStorageDir.mkdirs();
                            }
                            File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                            imageUri = Uri.fromFile(file);

                            final List<Intent> cameraIntents = new ArrayList<Intent>();
                            final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            final PackageManager packageManager = mContext.getPackageManager();
                            final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
                            for(ResolveInfo res : listCam) {
                                final String packageName = res.activityInfo.packageName;
                                final Intent i = new Intent(captureIntent);
                                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                                i.setPackage(packageName);
                                i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                cameraIntents.add(i);

                            }


                            mUploadMessage = uploadMsg;
                            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                            i.addCategory(Intent.CATEGORY_OPENABLE);
                            i.setType("image/*");
                            Intent chooserIntent = Intent.createChooser(i,"Image Chooser");
                            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

//                            PlaceholderFragment.this.startActivityForResult(chooserIntent,  FILECHOOSER_RESULTCODE);
                            getActivity().startActivityForResult(chooserIntent,  FILECHOOSER_RESULTCODE);

                        }

                        //For Android 3.0+
                        @SuppressWarnings("unused")
                        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){

                            mUploadMessage = uploadMsg;
                            File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyApp");
                            // Create the storage directory if it does not exist
                            if (! imageStorageDir.exists()){
                                imageStorageDir.mkdirs();
                            }
                            File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                            imageUri = Uri.fromFile(file);

                            final List<Intent> cameraIntents = new ArrayList<Intent>();
                            final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            final PackageManager packageManager = mContext.getPackageManager();
                            final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
                            for(ResolveInfo res : listCam) {
                                final String packageName = res.activityInfo.packageName;
                                final Intent i = new Intent(captureIntent);
                                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                                i.setPackage(packageName);
                                i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                cameraIntents.add(i);

                            }


                            mUploadMessage = uploadMsg;
                            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                            i.addCategory(Intent.CATEGORY_OPENABLE);
                            i.setType("image/*");
                            Intent chooserIntent = Intent.createChooser(i,"Image Chooser");
                            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
//                            PlaceholderFragment.this.startActivityForResult(chooserIntent,  FILECHOOSER_RESULTCODE);
                            getActivity().startActivityForResult(chooserIntent,  FILECHOOSER_RESULTCODE);
                        }

                        public boolean onShowFileChooser(
                                WebView webView, ValueCallback<Uri[]> filePathCallback,
                                WebChromeClient.FileChooserParams fileChooserParams) {
                            if(mFilePathCallback != null) {
                                mFilePathCallback.onReceiveValue(null);
                            }
                            mFilePathCallback = filePathCallback;

                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                // Create the File where the photo should go
                                File photoFile = null;
                                try {
                                    photoFile = createImageFile();
                                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                                } catch (IOException ex) {
                                    // Error occurred while creating the File
                                    Log.e(TAG, "Unable to create Image File", ex);
                                }

                                // Continue only if the File was successfully created
                                if (photoFile != null) {
                                    mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                            Uri.fromFile(photoFile));
                                } else {
                                    takePictureIntent = null;
                                }
                            }

                            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                            contentSelectionIntent.setType("image/*");

                            Intent[] intentArray;
                            if(takePictureIntent != null) {
                                intentArray = new Intent[]{takePictureIntent};
                            } else {
                                intentArray = new Intent[0];
                            }

                            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

//                            startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
                            getActivity().startActivityForResult(chooserIntent,  INPUT_FILE_REQUEST_CODE);
                            return true;
                        }

                    });
                }
            }
        }


        private File createImageFile() throws IOException {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            File imageFile = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            return imageFile;
        }

        public static void loadURL(String url) {
            if (mWebView != null) {
                Log.i("WEBVIEW",String.format("Load URL: %s",url));
                sCurrentUrl = url;
                mWebView.loadUrl(url);
            } else {
                Log.e("webview null","Error");
            }
        }

        public static void evalJavascript(String javascript) {
            if (mWebView != null) {
                mWebView.loadUrl( String.format("javascript:%s",javascript) );
            } else {
                Log.e("webview null","Error");
            }
        }

        // TODO: handle state between orientation changes

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            if (mWebView != null) {
                mWebView.saveState(outState);
            }
        }

        @Override
        public void onViewStateRestored(Bundle savedInstanceState) {
            super.onViewStateRestored(savedInstanceState);
            if (mWebView != null) {
                mWebView.restoreState(savedInstanceState);
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



        public void refreshWebView() {
            mWebView.reload();
        }
    }

}
