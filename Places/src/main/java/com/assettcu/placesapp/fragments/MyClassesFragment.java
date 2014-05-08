package com.assettcu.placesapp.fragments;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;

import com.assettcu.placesapp.R;
import com.assettcu.placesapp.adapters.CourseListAdapter;
import com.assettcu.placesapp.models.Course;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.FileNameMap;
import java.util.ArrayList;
import java.util.Iterator;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MyClassesFragment extends Fragment
{
    private CourseListAdapter courseListAdapter;
    private ListView classListView;
    private static final String FILENAME = "class_data.txt";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        courseListAdapter = new CourseListAdapter(getActivity());
        setHasOptionsMenu(true);

        try
        {
            InputStream inputStream = getActivity().getApplicationContext().openFileInput(FILENAME);

            if (inputStream != null)
            {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                String[] splitString;

                while ((receiveString = bufferedReader.readLine()) != null)
                {
                    splitString = receiveString.split(",");
                    courseListAdapter.addCourse(new Course(splitString[0], splitString[1]));
                }

                courseListAdapter.notifyDataSetChanged();
                inputStream.close();
            }
        } catch (FileNotFoundException e)
        {
            Log.e("class_fragment", "File not found: " + e.toString());
        } catch (IOException e)
        {
            Log.e("class_fragment", "Can not read file: " + e.toString());
        }

    }

    @Override
    public void onPause()
    {
        super.onPause();

        OutputStreamWriter writer = null;
        try
        {
            writer = new OutputStreamWriter(getActivity().getApplicationContext().openFileOutput(FILENAME, Context.MODE_PRIVATE));

            for(Course course : courseListAdapter.getCourses())
            {
                writer.write(course.toString() + "\n");
            }

            writer.close();
        }
        catch (IOException e)
        {
            Log.e("class_fragment", "File write failed: " + e.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_my_classes, container, false);

        classListView = (ListView) view.findViewById(R.id.class_list);
        classListView.setAdapter(courseListAdapter);

        ImageButton addButton = (ImageButton) view.findViewById(R.id.add_button);
        ImageButton deleteButton = (ImageButton) view.findViewById(R.id.delete_button);

        addButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DialogFragment newFragment = CourseDialogFragment.newInstance(null, courseListAdapter);
                newFragment.show(getActivity().getFragmentManager(), "dialog");
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(courseListAdapter.hasChecked())
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Are you sure you want to delete?");

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            courseListAdapter.removeChecked();
                        }
                    });

                    builder.setNegativeButton("No", null);

                    builder.show();
                }

            }
        });

        return view;
    }

}
