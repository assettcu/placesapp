package com.assettcu.placesapp.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.assettcu.placesapp.R;
import com.assettcu.placesapp.activities.HomeActivity;
import com.assettcu.placesapp.adapters.CourseListAdapter;
import com.assettcu.placesapp.models.Course;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

/**
 * Created by Aaron on 5/6/2014.
 */
public class CourseDialogFragment extends DialogFragment
{
    private static final String CLASS_ARG = "class";
    private static final String ADAPTER_ARG = "adapter";
    private Course course;
    private CourseListAdapter courseListAdapter;
    private ArrayAdapter<String> buildingSpinnerAdapter, classroomsSpinnerAdapter;

    private EditText editText;
    private Spinner buildingSpinner, classroomSpinner;

    public static CourseDialogFragment newInstance(Course course, CourseListAdapter listAdapter)
    {
        CourseDialogFragment fragment = new CourseDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(CLASS_ARG, course);
        args.putSerializable(ADAPTER_ARG, listAdapter);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            course = (Course) getArguments().getSerializable(CLASS_ARG);
            courseListAdapter = (CourseListAdapter) getArguments().getSerializable(ADAPTER_ARG);
        }

        classroomsSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item);
        buildingSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add a Class");

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Dialog d = CourseDialogFragment.this.getDialog();

                editText = (EditText) d.findViewById(R.id.class_name_edit_text);
                buildingSpinner = (Spinner) d.findViewById(R.id.building_spinner);

                String text = editText.getText().toString();
                if (text.length() > 15) text = text.substring(0, 15);

                Course mCourse = new Course(text,
                                 buildingSpinner.getSelectedItem().toString(),
                                 classroomSpinner.getSelectedItem().toString());

                courseListAdapter.addCourse(mCourse);
                courseListAdapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.setView(setupView());

        return builder.create();
    }

    private View setupView()
    {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_course_dialog, null);

        JsonArray json = ((HomeActivity) getActivity()).getBuildingsJsonArray();

        if (json.isJsonNull() == false)
        {
            String[] buildingArray = new String[json.size()];
            for (int i = 0; i < json.size(); i++)
            {
                JsonObject building = json.get(i).getAsJsonObject();
                if (building.isJsonNull() == false) buildingSpinnerAdapter.add(building.get("placename").getAsString());
            }

            buildingSpinnerAdapter.notifyDataSetChanged();

        } else
        {
            buildingSpinnerAdapter.add("Building Name");
            buildingSpinnerAdapter.notifyDataSetChanged();
        }

        buildingSpinner = (Spinner) view.findViewById(R.id.building_spinner);
        buildingSpinner.setAdapter(buildingSpinnerAdapter);
        buildingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                JsonArray json = ((HomeActivity) getActivity()).getBuildingsJsonArray();
                JsonObject building = json.get(position).getAsJsonObject();

                Log.d("Dialog", building.get("placename").toString() + " selected.");

                Ion.with(getActivity().getApplicationContext())
                        .load("http://places.colorado.edu/api/place/?id=" + building.get("placeid").getAsInt()).asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>()
                        {
                            @Override
                            public void onCompleted(Exception e, JsonObject result)
                            {
                                classroomsSpinnerAdapter.clear();
                                if (result.has("classrooms"))
                                {
                                    JsonArray classroomsJsonArray = result.get("classrooms").getAsJsonArray();
                                    for (int i = 0; i < classroomsJsonArray.size(); i++)
                                    {
                                        String room = classroomsJsonArray.get(i).getAsJsonObject().get("placename").getAsString();
                                        classroomsSpinnerAdapter.add(room);
                                    }
                                }

                                classroomsSpinnerAdapter.notifyDataSetChanged();
                            }
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        classroomSpinner = (Spinner) view.findViewById(R.id.classrooms_spinner);
        classroomSpinner.setAdapter(classroomsSpinnerAdapter);

        return view;
    }

}
