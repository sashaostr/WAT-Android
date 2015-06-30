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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartbee.wat.Model.ConfigNavBarButtons;
import com.smartbee.wat.Model.ConfigNavBarChildButtons;
import com.smartbee.wat.R;

public class NavigationExpandableListAdapter extends BaseExpandableListAdapter {

    private String[] mGroups;
    private String[][] mChildren;
    private ConfigNavBarButtons[] mButtons; // for icons

    private static final int[] EMPTY_STATE_SET = {};
    private static final int[] GROUP_EXPANDED_STATE_SET = { android.R.attr.state_expanded };
    private static final int[][] GROUP_STATE_SETS = { EMPTY_STATE_SET, GROUP_EXPANDED_STATE_SET };

    private final Context mContext;

    public NavigationExpandableListAdapter(Context context, String[] groups, String[][] children) {
        this.mContext = context;
        this.mGroups = groups;
        this.mChildren = children;
    }

    public NavigationExpandableListAdapter(Context context, String[] groups, String[][] children, ConfigNavBarButtons[] buttons) {
        this.mContext = context;
        this.mButtons = buttons;
        this.mGroups = groups;
        this.mChildren = children;
    }

    // Groups

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.navigation_group_item, null);
        }

        final ImageView indicator = (ImageView) convertView.findViewById(R.id.indicator);
        final TextView textView = (TextView) convertView.findViewById(R.id.text1);
        final ImageView iconView = (ImageView) convertView.findViewById(R.id.icon);

        final String item = (String) getGroup(groupPosition);

        textView.setText(item);
        displayIcon(iconView, groupPosition);

        if ( getChildrenCount(groupPosition) == 0 ) {
            indicator.setVisibility(View.INVISIBLE);
        }
        else
        {
            indicator.setVisibility(View.VISIBLE);
            int stateSetIndex = (isExpanded ? 1 : 0);
            Drawable drawable = indicator.getDrawable();
            drawable.setState(GROUP_STATE_SETS[stateSetIndex]);
        }

        return convertView;
    }

    @Override
    public int getGroupCount() {
        return mGroups.length;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroups[groupPosition];
    }

    // Children

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView =  inflater.inflate(R.layout.navigation_list_item, null);
        }

        final TextView textView = (TextView) convertView.findViewById(R.id.text1);
        final ImageView iconView = (ImageView) convertView.findViewById(R.id.icon);

        final String item = (String) getChild(groupPosition, childPosition);

        textView.setText(item);
        displayIcon(iconView, groupPosition, childPosition);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChildren[groupPosition].length;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mChildren[groupPosition][childPosition];
    }

    // Settings

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    // Icons

    private void displayIcon(ImageView iconView, int groupPosition)
    {
        //Log.i("GROUP",String.format("#%d",groupPosition));
        ConfigNavBarButtons btn = mButtons[groupPosition];
        if (btn != null) {
            if(btn.getIcon()!=null) {
                String icon = btn.getIcon();
                displayIcon(iconView, icon);
            }
        }
    }

    private void displayIcon(ImageView iconView, int groupPosition, int childPosition)
    {
        //Log.i("CHILD",String.format("#%d > #%d",groupPosition, childPosition));
        ConfigNavBarChildButtons btn = mButtons[groupPosition].getChildren()[childPosition];
        if (btn != null) {
            String icon = btn.getIcon();
            displayIcon(iconView, icon);
        }
    }

    private void displayIcon(ImageView iconView, String icon) {
        int resId = mContext.getResources().getIdentifier("ic_action_"+icon,"drawable",mContext.getPackageName()) |
                mContext.getResources().getIdentifier(icon,"drawable",mContext.getPackageName()) ;
        if (resId>0) {
            iconView.setImageDrawable( mContext.getResources().getDrawable(resId) );
            iconView.setVisibility(View.VISIBLE);
        } else {
            iconView.setVisibility(View.GONE); // removes icon from 'recycled' cells
        }
    }

}
