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

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.smartbee.wat.Helper.Constants;
import com.smartbee.wat.Model.Config;
import com.smartbee.wat.Model.LocalRemoteFilesConfig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.smartbee.wat.R;

// This app wide class loads our WAT JSON config (config.json)

public class WATApp extends Application
{
    private static Config sConfig; // WAT App config
    private static LocalRemoteFilesConfig sLocalRemoteFilesConfig;
    private WATApp mApp;
    private boolean mLoadingFromUserSpecifiedUrl = false;

    @Override
    public void onCreate()
    {
        super.onCreate();
        mApp = this;

        if (sConfig == null)
        {
            loadConfig(false);
        }
    }

    public Config getConfig()
    {
        return getConfig(false);
    }

    public Config getConfig(boolean forceReload) {
        if (sConfig != null && !forceReload)
        {
            return sConfig;
        }
        else
        {
            sConfig = null;
            if ( ! loadConfig(forceReload) )
            {
                Log.e("Could not load app config.","Config Error"); // If this happens check JSON maps to Config Model
                return null;
            }
            else
            {
                return sConfig;
            }
        }
    }

    private boolean mIsLoading = false;

    private boolean loadConfig(boolean forceReload)
    {
        if (mIsLoading) return false;
        mIsLoading = true;
        boolean configLoadedAndValid = false;

        if (Constants.ALLOW_REMOTE_CONFIG_CHANGE) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mApp);
            String remoteConfigUrl = sp.getString(Constants.SHARED_PREF_CONFIG_URL, null);
            if (remoteConfigUrl != null && !remoteConfigUrl.equals("")) {
                try {
                    new DownloadConfigFileFromWebTask().execute(remoteConfigUrl);
                    return false;
                } catch (Exception ex) {
                    Log.e(Constants.TAG, "Issue loading saved config URL file: " + ex.getMessage());
                }
            }
        }

        boolean localFileConfigValid = loadLocalRemoteFilesConfig();
        if (Constants.ALLOW_REMOTE_CONFIG_CHANGE && localFileConfigValid && !sLocalRemoteFilesConfig.getConfigJsonUri().equals("")) {
            //if (!sLocalRemoteFilesConfig.getConfigJsonUri().equals("")) {
                //throw new Exception("This should pull the file down and load it as the config");
                try {
                    new DownloadConfigFileFromWebTask().execute(sLocalRemoteFilesConfig.getConfigJsonUri());
                    return false;
                    //if (!fileText.equals("")) {
                    //    Log.e(Constants.TAG, "Config could not be pulled down, load from local file.");
                    //}
                } catch (Exception ex) {
                    Log.e(Constants.TAG, "Issue loading remote config file: " + ex.getMessage());
                    //Load local
                    //configLoadedAndValid = loadLocalConfig();
                }
            //}
        } else {
            configLoadedAndValid = loadLocalConfig();
            mIsLoading = false;
        }


//        if (sConfig == null || !configLoadedAndValid) {
//            configLoadedAndValid = loadLocalConfig();
//        }

//        if (forceReload) {
//            Log.d(Constants.TAG, "Config loaded, starting activity");
//            Intent intent = new Intent(this, WATActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        }

        return configLoadedAndValid;
    }

    public void loadRemoteConfigWithUrl(String configUrl) {
        mLoadingFromUserSpecifiedUrl = true;
        new DownloadConfigFileFromWebTask().execute(configUrl);
    }

    private class DownloadConfigFileFromWebTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            boolean validConfigLoaded = false;

            if (params.length == 0 || params[0].equals("")) {
                Log.e(Constants.TAG, "No file URLs passed to DownloadConfigFileFromWebTask");
            } else {
                try {
                    if (mLoadingFromUserSpecifiedUrl) {
                        mLoadingFromUserSpecifiedUrl = false;
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mApp);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString(Constants.SHARED_PREF_CONFIG_URL, params[0]);
                        editor.commit();
                    }
                    String fileText = loadRemoteFile(params[0]); // loads files.json
                    Gson gson = new Gson();
                    sConfig = gson.fromJson(fileText, Config.class);
                    sConfig.handlePostLoad();
                    System.out.println(gson);
                    if (sConfig != null && !sConfig.getHomeURL().equals("")) {
                        validConfigLoaded = true;
                    }
                } catch (Exception ex) {
                    Log.e(Constants.TAG, "Issue loading remote config file: " + ex.getMessage());
                    validConfigLoaded = loadLocalConfig();
                }
            }
            mIsLoading = false;
            return validConfigLoaded;
        }

        @Override
        protected void onPostExecute(Boolean validConfigLoaded) {
            super.onPostExecute(validConfigLoaded);

            if (validConfigLoaded) {
                Log.d(Constants.TAG, "Config loaded, starting activity");
            //    Intent intent = new Intent(mApp, WATActivity.class);
            //    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            //    startActivity(intent);
                Intent broadcast = new Intent();
                broadcast.setAction(Constants.BROADCAST_CONFIG_LOADED);
                mApp.sendBroadcast(broadcast);
            }
        }

        //Pulled from http://developer.android.com/training/basics/network-ops/connecting.html
        private String loadRemoteFile(String fileUrl) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 500;

            try {
                URL url = new URL(fileUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d(Constants.TAG, "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string

                String contentAsString = readIt(is, len);
                return contentAsString;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        //Pulled from http://developer.android.com/training/basics/network-ops/connecting.html
        public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {

            ByteArrayOutputStream byos = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[stream.available()];
            while ((nRead = stream.read(data, 0, data.length)) != -1)
               byos.write(data, 0, nRead);
            byos.flush();
            String jsonString = new String(byos.toByteArray(), "UTF-8");

            return jsonString;
        }
    }



    private boolean loadLocalRemoteFilesConfig() {
        boolean isValid = false;
        AssetManager assetManager = getResources().getAssets();
        try {
            String configFilename = getString(R.string.local_remote_files); // Using strings.xml to allow link to a localised JSON in future. eg. res/values-en/strings.xml
            InputStream inputStream = assetManager.open( configFilename + ".json" ); // "files.json"
            int size = inputStream.available();
            byte[] bytes = new byte[size];
            inputStream.read(bytes);
            inputStream.close();
            String jsonString = new String(bytes, "UTF-8");
            jsonString = translateConfigForModel(jsonString); // adds support for new W3C web app config
            Gson gson = new Gson();
            sLocalRemoteFilesConfig = gson.fromJson(jsonString, LocalRemoteFilesConfig.class);
            sLocalRemoteFilesConfig.handlePostLoad();
            System.out.println(gson);
            isValid = true;
        } catch (IOException e) {
            e.printStackTrace();
            isValid = false;
        }
        return isValid;
    }

    private boolean loadLocalConfig() {
        boolean isValid = false;
        AssetManager assetManager = getResources().getAssets();
        try {
            String configFilename = getString(R.string.app_config); // Using strings.xml to allow link to a localised JSON in future. eg. res/values-en/strings.xml
            InputStream inputStream = assetManager.open( configFilename + ".json" ); // "config.json"
            int size = inputStream.available();
            byte[] bytes = new byte[size];
            inputStream.read(bytes);
            inputStream.close();
            String jsonString = new String(bytes, "UTF-8");
            jsonString = translateConfigForModel(jsonString); // adds support for new W3C web app config
            Gson gson = new Gson();
            sConfig = gson.fromJson(jsonString, Config.class);
            sConfig.handlePostLoad();
            System.out.println(gson);
            isValid = true;
        } catch (IOException e) {
            e.printStackTrace();
            isValid = false;
        }
        return isValid;
    }

    // if config contains the "start_url" then we are using the W3C Web App Manifest config
    private boolean isW3CWebAppManifest(String text)
    {
        Pattern p = Pattern.compile("[\"']{1}start_url[\"']{1}\\s?:", Pattern.MULTILINE);
        Matcher m = p.matcher(text);
        return m.find();
    }

    private String translateConfigForModel(String jsonString) {
        if (isW3CWebAppManifest(jsonString)) {
            jsonString = jsonString.replaceAll("[\"']{1}start_url[\"']{1}\\s?:", "\"homeURL\":");
            // remove all "wat_" key prefixes
            Pattern p = Pattern.compile("[\"']+(wat_)(.+)[\"']+[\\s]?:", Pattern.MULTILINE);
            Matcher m = p.matcher(jsonString);
            if(m.find()) {
                jsonString = m.replaceAll("\"$2\":");
            }
            //System.out.println(jsonString);
        }
        return jsonString;
    }

}
