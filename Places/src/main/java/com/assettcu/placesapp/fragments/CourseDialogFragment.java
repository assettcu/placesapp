package com.assettcu.placesapp.fragments;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.assettcu.placesapp.HomeActivity;
import com.assettcu.placesapp.R;
import com.assettcu.placesapp.adapters.CourseListAdapter;
import com.assettcu.placesapp.models.Course;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Created by Aaron on 5/6/2014.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class CourseDialogFragment extends DialogFragment
{
    private static final String CLASS_ARG = "class";
    private static final String ADAPTER_ARG = "adapter";
    private Course course;
    private CourseListAdapter courseListAdapter;
    private ArrayAdapter<String> spinnerAdapter;

    private EditText editText;
    private Spinner spinner;

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
        if(getArguments() != null)
        {
            course = (Course) getArguments().getSerializable(CLASS_ARG);
            courseListAdapter = (CourseListAdapter) getArguments().getSerializable(ADAPTER_ARG);
        }
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
                spinner = (Spinner) d.findViewById(R.id.building_spinner);

                String text = editText.getText().toString();
                if(text.length() > 12) text = text.substring(0, 12);

                Course mCourse = new Course(text, spinner.getSelectedItem().toString());

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
        View view = inflater.inflate(R.layout.fragment_class_dialog, null);

        JsonArray json = ((HomeActivity) getActivity()).getBuildingsJsonArray();

        if(json.isJsonNull() == false)
        {
            String[] spinnerArray = new String[json.size()];
            for(int i = 0; i < json.size(); i++)
            {
                JsonObject building = json.get(i).getAsJsonObject();
                if(building.isJsonNull() == false) spinnerArray[i] = building.get("placename").getAsString();
            }

            spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        }
        else
        {
            String[] spinnerArray = {"Building Name"};
            spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        }

        spinner = (Spinner) view.findViewById(R.id.building_spinner);
        spinner.setAdapter(spinnerAdapter);

        return view;
    }

}
