package com.assettcu.placesapp.adapters;

import android.app.Activity;
import android.text.Html;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.assettcu.placesapp.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Aaron on 4/24/2014.
 * Deals with displaying an expandable list.
 */
public class BuildingDisplayListAdapter extends BaseExpandableListAdapter
{
    private SparseArray<String[]> groups;
    private SparseArray<String> groupNames;
    private LayoutInflater inflater;

    public BuildingDisplayListAdapter(Activity activity)
    {
        groups = new SparseArray<String[]>();
        groupNames = new SparseArray<String>();
        inflater = activity.getLayoutInflater();
    }

    public void setGroupData(int groupPosition, String[] values, String groupName)
    {
        if(values.length == 0) return;

        groups.put(groupPosition, values);
        groupNames.put(groupPosition, groupName);
    }

    @Override
    public int getGroupCount()
    {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return groups.get(groupPosition).length;
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return null;
    }

    @Override
    public String getChild(int groupPosition, int childPosition)
    {
        return groups.get(groupPosition)[childPosition];
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return 0;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.adapter_group_row, null);
            TextView text = (TextView) convertView.findViewById(R.id.group_row_text);
            text.setText(groupNames.get(groupPosition));
        }
        else
        {
            TextView text = (TextView) convertView.findViewById(R.id.group_row_text);
            text.setText(groupNames.get(groupPosition));
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.adapter_child_row, null);
            TextView text = (TextView) convertView.findViewById(R.id.child_row_text);
            text.setText(Html.fromHtml(groups.get(groupPosition)[childPosition]));
        }
        else
        {
            TextView text = (TextView) convertView.findViewById(R.id.child_row_text);
            text.setText(Html.fromHtml(groups.get(groupPosition)[childPosition]));
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return groupPosition == 2;
    }
}
