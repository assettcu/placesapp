package com.assettcu.placesapp.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.assettcu.placesapp.R;

/**
 * Created by Aaron on 5/16/2014.
 */
public class ReportABugFragment extends Fragment
{

    private Spinner interactionSpinner, typeSpinner;
    private EditText editText;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_report_a_bug, container, false);

        interactionSpinner = (Spinner) view.findViewById(R.id.interaction_spinner);
        ArrayAdapter<CharSequence> interactionAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.interaction_points, R.layout.spinner_item);
        interactionSpinner.setAdapter(interactionAdapter);

        typeSpinner = (Spinner) view.findViewById(R.id.type_spinner);
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.bug_types, R.layout.spinner_item);
        typeSpinner.setAdapter(typeAdapter);

        Button report = (Button) view.findViewById(R.id.reportButton);
        setReportButtonListener(report);

        editText = (EditText) view.findViewById(R.id.commentText);

        return view;
    }

    public void sendReport()
    {
        Toast.makeText(getActivity(), "Sent report about problems with "
                + interactionSpinner.getSelectedItem().toString() + " regarding a(n) "
                + typeSpinner.getSelectedItem().toString()
                + " type error or bug. Thanks for your input!", Toast.LENGTH_LONG).show();
        editText.setText("", null);
    }


    private void setReportButtonListener(Button report)
    {
        report.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if(editText.getText().length() == 0)
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setPositiveButton("Alright...", null);
                    dialog.setTitle("Please include a comment.");
                    dialog.show();
                }
                else
                {
                   sendReport();
                }

            }
        });
    }
}
