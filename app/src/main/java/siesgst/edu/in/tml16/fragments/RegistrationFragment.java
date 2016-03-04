package siesgst.edu.in.tml16.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Regex;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.mobsandgeeks.saripaar.annotation.TextRule;

import siesgst.edu.in.tml16.R;
import siesgst.edu.in.tml16.TMLApplication;
import siesgst.edu.in.tml16.utils.ConnectionUtils;
import siesgst.edu.in.tml16.utils.LocalDBHandler;
import siesgst.edu.in.tml16.utils.OnlineDBDownloader;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegistrationFragment extends Fragment implements Validator.ValidationListener {

    OnlineDBDownloader db;
    @Required(order = 1)
    EditText fullName;
    @Required(order = 2)
    @Email(order = 3, message = "Enter valid email address")
    EditText emailID;
    @Required(order = 4)
    @Regex(order = 5, pattern = "[0-9]+", message = "Phone number should contain only digits")
    @TextRule(order = 6, minLength = 10, maxLength = 10, message = "Please enter a 10 digit number")
    EditText phone;
    EditText division;
    @Required(order = 7)
    @Regex(order = 8, pattern = "[0-9A-Za-z]+", message = "Phone number should contain only alphanumeric characters")
    EditText rollNO;

    @Required(order = 9)
    AutoCompleteTextView college;

    String year, branch, event = "";

    SharedPreferences sharedPreferences;

    ProgressDialog progressDialog;

    Tracker mTracker;
    Validator validator;

    public RegistrationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        db = new OnlineDBDownloader(getActivity());

        validator = new Validator(this);
        validator.setValidationListener(this);

        fullName = (EditText) view.findViewById(R.id.full_name);
        emailID = (EditText) view.findViewById(R.id.email_id);
        phone = (EditText) view.findViewById(R.id.phone_no);
        division = (EditText) view.findViewById(R.id.div);
        rollNO = (EditText) view.findViewById(R.id.roll_no);

        sharedPreferences = getActivity().getSharedPreferences("TML", Context.MODE_PRIVATE);

        college = (AutoCompleteTextView) view.findViewById(R.id.college);
        String[] colleges = {"Thakur College", "TSEC", "VJTI", "Kalsekar College", "LT College", "Saboo Siddik", "Vidkyalankar", "Xaviers", "TS Chanakya", "Datta Meghe", "Indira Gandhi", "Vasant Dada Patil", "Watumull", "Saraswati", "KC College", "Pillai", "RAIT", "Fr. Agnel", "VESIT", "KJ Somaiya", "MGM Khedkar", "Bharti Vidyapeeth", "Shah and Anchor", "DJ Sanghvi", "RGIT", "AC Patil", "Sardar Patil", "Rizvi", "RA Podar", "DY Patil, Belapur", "Dr. KTV Reddy", "Terna", "Don Bosco", "SIES Graduate School of Technology", "Other"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.select_dialog_item, colleges);
        college.setAdapter(adapter);


        Spinner spinnerYear = (Spinner) view.findViewById(R.id.spinner_year);
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                year = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner spinnerBranch = (Spinner) view.findViewById(R.id.spinner_branch);
        spinnerBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                branch = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner spinnerEvent = (Spinner) view.findViewById(R.id.spinner_event);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, (new LocalDBHandler(getActivity())).getAllEventNames());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEvent.setAdapter(dataAdapter);
        spinnerEvent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                event = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        AppCompatButton submit = (AppCompatButton) view.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                validator.validate();
            }
        });

        TMLApplication application = (TMLApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        return view;
    }

    public void onValidationFailed(View failedView, Rule<?> failedRule) {
        String message = failedRule.getFailureMessage();

        if (failedView instanceof EditText) {
            failedView.requestFocus();
            ((EditText) failedView).setError(message);
        } else {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    public void onValidationSucceeded() {
        progressDialog = ProgressDialog.show(getActivity(), "Registering", "Please wait...");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new Thread() {
                    @Override
                    public void run() {
                        if ((new ConnectionUtils(getActivity()).checkConnection())) {
                            db.submitRegData(fullName.getText().toString(), emailID.getText().toString(), phone.getText().toString(), year, branch, college.getText().toString(), division.getText().toString(), rollNO.getText().toString(), event);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), sharedPreferences.getString("reg_status", "") + " \nComplete your payment at the registration desk.", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                    mTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("Register for friend")
                                            .setAction("Register")
                                            .build());
                                }
                            });
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "Please check your internet connection..", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    //Snackbar.make(v, "Please check your internet connection..", Snackbar.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }.start();
            }
        }, 4000);
    }
}