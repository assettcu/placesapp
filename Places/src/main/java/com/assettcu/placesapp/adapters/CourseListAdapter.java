package com.assettcu.placesapp.adapters;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.assettcu.placesapp.R;
import com.assettcu.placesapp.models.Course;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Aaron on 5/6/2014.
 */
public class CourseListAdapter extends BaseAdapter implements Serializable
{
    private ArrayList<Course> courses;
    private Context mContext;

    public CourseListAdapter(Context c)
    {
        courses = new ArrayList<Course>();
        mContext = c;
    }

    public boolean hasChecked()
    {
        for(Iterator<Course> it = courses.iterator(); it.hasNext();)
        {
            if(it.next().isSelected()) return true;
        }

        return false;
    }

    public void removeChecked()
    {
        for(Iterator<Course> it = courses.iterator(); it.hasNext();)
        {
            if(it.next().isSelected()) it.remove();
        }

        notifyDataSetChanged();
    }

    public ArrayList<Course> getCourses()
    {
        return courses;
    }

    public void addCourse(Course course)
    {
        courses.add(course);
    }

    @Override
    public int getCount()
    {
        return courses.size();
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View classListView;

        classListView = new View(mContext);
        classListView = inflater.inflate(R.layout.adapter_list_course, null);

        Course course = courses.get(position);

        TextView className = (TextView) classListView.findViewById(R.id.class_list_name);
        TextView buildingName = (TextView) classListView.findViewById(R.id.building_list_name);
        CheckBox checkBox = (CheckBox) classListView.findViewById(R.id.class_check_box);

        className.setText(course.getName());
        buildingName.setText(course.getCname());

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                courses.get(position).setSelected(isChecked);
            }
        });

        return classListView;
    }
}
