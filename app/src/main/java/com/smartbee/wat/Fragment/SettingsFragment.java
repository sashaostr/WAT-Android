/*
Web App Template for Android 

Copyright (c) Microsoft Corporation

All rights reserved. 

MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the ""Software""), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED *AS IS*, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

*/

package com.smartbee.wat.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.smartbee.wat.Adapter.SettingsItemsAdapter;
import com.smartbee.wat.Model.Config;
import com.smartbee.wat.Model.ConfigSettingsItems;
import com.smartbee.wat.R;
import com.smartbee.wat.WATActivity;
import com.smartbee.wat.WATApp;

public class SettingsFragment extends DialogFragment {

    private Config mConfig;

    private Button mButtonPrivacy;
    private ListView mListView;
    private SettingsItemsAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        mButtonPrivacy = (Button) view.findViewById(R.id.buttonPrivacy);
        mListView = (ListView) view.findViewById(R.id.listView);

        getSettingsItems(); // TODO: test with reload json config

        // click listeners
        mButtonPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleClick(mConfig.getSettings().getPrivacyUrl(), false);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ConfigSettingsItems item = mConfig.getSettings().getItems()[position];
                handleClick(item.getPage(), item.isLoadInApp());
            }
        });

        // defaults
        String title = getString(R.string.settings); //"Settings";

        // bundle
        if ( getArguments() != null) {
            title = getArguments().getString("title");
        }

        getDialog().setTitle(title);

        return view;
    }

    private void getSettingsItems() {
        mConfig = ((WATApp) getActivity().getApplication()).getConfig();
        if( mConfig != null ) {
            if (mConfig.getSettings().getItems().length > 0) {
                displaySettingsItems(mConfig.getSettings().getItems());
            }
            if (mConfig.getSettings().getPrivacyUrl().length() > 0) {
                mButtonPrivacy.setVisibility(View.VISIBLE);
            }
        }
    }

    private void displaySettingsItems(ConfigSettingsItems[] items) {
        mAdapter = new SettingsItemsAdapter(getActivity(), R.layout.settings_list_item, items);
        mListView.setAdapter(mAdapter);
    }

    private void handleClick(String url, Boolean isLoadInApp) {
        if (isLoadInApp == true) {
            WATActivity.PlaceholderFragment.loadURL(url);
        } else {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit(); // close settings fragment after selection
    }


}
