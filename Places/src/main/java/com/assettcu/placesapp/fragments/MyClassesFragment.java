package com.assettcu.placesapp.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;

import com.assettcu.placesapp.R;
import com.assettcu.placesapp.adapters.CourseListAdapter;
import com.assettcu.placesapp.models.Course;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MyClassesFragment extends Fragment
{
    private CourseListAdapter courseListAdapter;
    private ListView classListView;
    private ImageButton addButton, deleteButton;
    private CheckBox checkAll;

    private static final String FILENAME = "class_data.txt";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        courseListAdapter = new CourseListAdapter(getActivity());
        readCourseFile();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        writeCourseFile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_my_classes, container, false);
        container.setBackgroundColor(getResources().getColor(android.R.color.white));

        classListView = (ListView) view.findViewById(R.id.class_list);
        classListView.setAdapter(courseListAdapter);

        // Set the all checked button to change all the list items checkboxes to checked
        checkAll = (CheckBox) view.findViewById(R.id.check_all);
        checkAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                courseListAdapter.setAllChecked(isChecked);
            }
        });

        addButton = (ImageButton) view.findViewById(R.id.add_button);
        deleteButton = (ImageButton) view.findViewById(R.id.delete_button);

        // Setup the add button to display a dialog that prompts the user to enter a new class
        // and then writes the changes they made after the class has been added to the list
        addButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DialogFragment newFragment = CourseDialogFragment.newInstance(null, courseListAdapter);
                newFragment.show(getActivity().getSupportFragmentManager(), "dialog");
                writeCourseFile();
            }
        });

        // Setup the delete button to check for items to delete, if there are items
        // then display a dialog asking the user if it's ok to remove the items,
        // if it is, remove all the items are selected from the list
        deleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (courseListAdapter.hasChecked())
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Are you sure you want to delete?");

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            courseListAdapter.removeChecked();
                            writeCourseFile();
                            checkAll.setChecked(false);
                        }
                    });

                    builder.setNegativeButton("No", null);
                    builder.show();
                }
            }
        });

        return view;
    }

    // Rewrites the private app file to store data about the list
    public void writeCourseFile()
    {
        OutputStreamWriter writer = null;
        try
        {
            writer = new OutputStreamWriter(getActivity().getApplicationContext().openFileOutput(FILENAME, Context.MODE_PRIVATE));

            for (Course course : courseListAdapter.getCourses())
            {
                writer.write(course.toString() + "\n");
            }

            writer.close();
        } catch (IOException e)
        {
            Log.e("class_fragment", "File write failed: " + e.toString());
        }
    }

    // Reads in the course file on fragment startup to re-input the class list
    // into the appropriate adapter
    public void readCourseFile()
    {
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
                    courseListAdapter.addCourse(new Course(splitString[0], splitString[1], splitString[2]));
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

}
