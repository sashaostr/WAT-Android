/*
Web App Template for Android 

Copyright (c) Microsoft Corporation

All rights reserved. 

MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the ""Software""), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED *AS IS*, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

*/

package com.smartbee.wat.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.smartbee.wat.Model.ConfigSettingsItems;
import com.smartbee.wat.R;

public class SettingsItemsAdapter extends ArrayAdapter<ConfigSettingsItems> {
    Context mContext;
    int mLayoutResourceId;
    ConfigSettingsItems[] mObjects;

    public SettingsItemsAdapter(Context context, int layoutResourceId, ConfigSettingsItems[] objects){
        super(context,layoutResourceId,objects);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
        mObjects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View cell = convertView;

        final ConfigSettingsItems currentItem = getItem(position);

        if (cell == null)
        {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            cell = inflater.inflate(mLayoutResourceId, parent, false);
        }

        cell.setTag(currentItem);

        final TextView text1 = (TextView) cell.findViewById(R.id.text1);
        text1.setText( currentItem.getTitle() );

        return cell; //return super.getView(position, convertView, parent);
    }
}
