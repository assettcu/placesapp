package com.assettcu.placesapp.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

    private Spinner spinner;
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

        spinner = (Spinner) view.findViewById(R.id.interactionSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.interaction_points, R.layout.spinner_item);
        spinner.setAdapter(adapter);

        Button report = (Button) view.findViewById(R.id.reportButton);
        setReportButtonListener(report);

        editText = (EditText) view.findViewById(R.id.commentText);

        return view;
    }

    public void sendReport()
    {
        Toast.makeText(getActivity(), "Sent report about "
                + spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
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
                    dialog.setNegativeButton("No", null);
                    dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            sendReport();
                        }
                    });

                    dialog.setTitle("Are you sure you want to send a report with no comments?");
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
