package com.assettcu.placesapp.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.assettcu.placesapp.R;

import java.util.ArrayList;

/**
 * Created by Aaron on 4/24/2014.
 * Deals with displaying an exandable list.
 */
public class BuildingDisplayListAdapter extends BaseExpandableListAdapter
{
    private ArrayList<ArrayList<String>> groups;
    private ArrayList<String> groupNames;
    private LayoutInflater inflater;

    public BuildingDisplayListAdapter(Activity activity)
    {
        groups = new ArrayList<ArrayList<String>>();
        groupNames = new ArrayList<String>();
        inflater = activity.getLayoutInflater();
    }

    public void addDataToGroup(int groupPosition, String[] values, String groupName)
    {
        ArrayList<String> data = new ArrayList<String>();

        for(String string : values)
        {
            data.add(string);
        }

        groups.add(groupPosition, data);
        groupNames.add(groupPosition, groupName);
    }

    @Override
    public int getGroupCount()
    {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return groups.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return null;
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

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {

        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.adapter_child_row, null);

            TextView text = (TextView) convertView.findViewById(R.id.child_row_text);
            text.setText(groups.get(groupPosition).get(childPosition));
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return false;
    }
}
