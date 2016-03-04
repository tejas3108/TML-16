package siesgst.edu.in.tml16;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Regex;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.mobsandgeeks.saripaar.annotation.TextRule;

import org.json.JSONArray;
import org.json.JSONObject;

import siesgst.edu.in.tml16.utils.ConnectionUtils;
import siesgst.edu.in.tml16.utils.DataHandler;
import siesgst.edu.in.tml16.utils.OnlineDBDownloader;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, Validator.ValidationListener {

    private AppCompatButton mGooleplus;
    private AppCompatButton mSkip;
    private GoogleApiClient mGoogleApiClient;

    private ProgressDialog progressDialog;

    Intent i;

    @Required(order = 1)
    @Regex(order = 2, pattern = "[0-9]+", message = "Phone number should contain only digits")
    @TextRule(order = 3, minLength = 10, maxLength = 10, message = "Please enter a 10 digit number")
    EditText phone;
    EditText division;
    @Required(order = 4)
    @Regex(order = 5, pattern = "[0-9A-Za-z]+", message = "Phone number should contain only alphanumeric characters")
    EditText rollNO;

    @Required(order = 6)
    AutoCompleteTextView college;

    String year, branch = "";

    String userName, userEmail, userPhone, userCollege, userDiv, userRoll;

    Validator validator;
    OnlineDBDownloader db;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                sharedPreferences = getSharedPreferences("TML", MODE_PRIVATE);
                editor = sharedPreferences.edit();

                boolean isFirstTime = sharedPreferences.getBoolean("firstTime", true);
                if(isFirstTime) {
                    startActivity(new Intent(LoginActivity.this, IntroActivity.class));
                    editor.putBoolean("firstTime", false);
                    editor.apply();
                }
            }
        });

        t.start();

        setContentView(R.layout.activity_login);

        validator = new Validator(this);
        validator.setValidationListener(this);

        //Set up Goolge Sign in APIs
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mSkip = (AppCompatButton) findViewById(R.id.skip);
        mSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        //Handle G+ Sign in button action
        mGooleplus = (AppCompatButton) findViewById(R.id.google);
        mGooleplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialog.show(LoginActivity.this, "", "Loading...");
                if ((new ConnectionUtils(LoginActivity.this)).checkConnection()) {
                    signIn();
                } else {
                    Toast.makeText(LoginActivity.this, "Error Signing in.\nPlease check your internet connection or try again.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
            setResult(1);
            finish();
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, 0);
    }


    //Handle Callback of the Login Request action
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 0) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }


    //Get user information after successful login
    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {

            GoogleSignInAccount acct = result.getSignInAccount();

            i = new Intent(this, HomeActivity.class);
            userName = acct.getDisplayName();
            userEmail = acct.getEmail();
            i.putExtra("username", acct.getDisplayName());
            i.putExtra("email", acct.getEmail());
            try {
                i.putExtra("profile_pic", acct.getPhotoUrl().toString());
            } catch (NullPointerException e) {
                i.putExtra("profile_pic", "");
            }
            new UserDetailDownload().execute(acct.getEmail());
        } else {
            Toast.makeText(LoginActivity.this, "Error Signing in.\nPlease check your internet connection or try again.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(LoginActivity.this, "Error Signing in.\nPlease check your internet connection or try again.", Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void setFinalCall(Intent intent, boolean bool) {
        intent.putExtra("user_exists", bool);
        setResult(RESULT_OK, intent);
        progressDialog.dismiss();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1000);
    }

    private class UserDetailDownload extends AsyncTask<String, Void, Void> {
        JSONArray userArray;
        JSONArray eventArray;
        JSONObject userDetailsObject;

        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;

        boolean status;

        OnlineDBDownloader downloader;

        @Override
        protected Void doInBackground(String... params) {
            downloader = new OnlineDBDownloader(LoginActivity.this);
            sharedPreferences = getSharedPreferences("TML", MODE_PRIVATE);
            editor = sharedPreferences.edit();
            downloader.sendUserEmail(params[0]);
            status = downloader.getUserStatus();
            if (status) {
                userArray = downloader.getUserDetailsArray();
                eventArray = downloader.getRegEventDetailsArray();
                new DataHandler(LoginActivity.this).pushRegEvents(eventArray);
                userDetailsObject = userArray.optJSONObject(0);
                editor.putString("uID", userDetailsObject.optString("uID"));
                editor.putString("uPhone", userDetailsObject.optString("uPhone"));
                editor.putString("uYear", userDetailsObject.optString("uYear"));
                editor.putString("uBranch", userDetailsObject.optString("uBranch"));
                editor.putString("uCollege", userDetailsObject.optString("uCollege"));
                editor.putString("uRoll", userDetailsObject.optString("uRoll"));
                editor.putString("uDivision", userDetailsObject.optString("uDivision"));
                editor.apply();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (status) {
                setFinalCall(i, true);
            } else {
                setContentView(R.layout.user_details);
                progressDialog.dismiss();

                db = new OnlineDBDownloader(LoginActivity.this);

                phone = (EditText) findViewById(R.id.phone_no);
                division = (EditText) findViewById(R.id.div);
                rollNO = (EditText) findViewById(R.id.roll_no);
                college = (AutoCompleteTextView) findViewById(R.id.college);
                String[] colleges = {"Thakur College", "TSEC", "VJTI", "Kalsekar College", "LT College", "Saboo Siddik", "Vidkyalankar", "Xaviers", "TS Chanakya", "Datta Meghe", "Indira Gandhi", "Vasant Dada Patil", "Watumull", "Saraswati", "KC College", "Pillai", "RAIT", "Fr. Agnel", "VESIT", "KJ Somaiya", "MGM Khedkar", "Bharti Vidyapeeth", "Shah and Anchor", "DJ Sanghvi", "RGIT", "AC Patil", "Sardar Patil", "Rizvi", "RA Podar", "DY Patil, Belapur", "Dr. KTV Reddy", "Terna", "Don Bosco", "SIES Graduate School of Technology", "Other"};

                ArrayAdapter<String> adapter = new ArrayAdapter<String>
                        (LoginActivity.this, android.R.layout.select_dialog_item, colleges);
                college.setAdapter(adapter);

                Spinner spinnerYear = (Spinner) findViewById(R.id.spinner_year);
                spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        year = parent.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                Spinner spinnerBranch = (Spinner) findViewById(R.id.spinner_branch);
                spinnerBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        branch = parent.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                AppCompatButton button = (AppCompatButton) findViewById(R.id.submit);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        validator.validate();
                    }
                });
            }
        }
    }

    public void onValidationFailed(View failedView, Rule<?> failedRule) {
        String message = failedRule.getFailureMessage();

        if (failedView instanceof EditText) {
            failedView.requestFocus();
            ((EditText) failedView).setError(message);
        } else {
            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }

    public void onValidationSucceeded() {
        new SendNewUserDetails().execute();
        setFinalCall(i, false);
    }

    private class SendNewUserDetails extends AsyncTask<Void, Void, Void> {

        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;

        @Override
        protected void onPreExecute() {
            userPhone = phone.getText().toString();
            userCollege = college.getText().toString();
            userDiv = division.getText().toString();
            userRoll = rollNO.getText().toString();

        }

        @Override
        protected Void doInBackground(Void... params) {
            sharedPreferences = getSharedPreferences("TML", MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.putString("uPhone", userPhone);
            editor.putString("uYear", year);
            editor.putString("uBranch", branch);
            editor.putString("uCollege", userCollege);
            editor.putString("uRoll", userRoll);
            editor.putString("uDivision", userDiv);
            editor.remove("user_exists");
            editor.putBoolean("user_exists", true);
            editor.apply();
            if ((new ConnectionUtils(LoginActivity.this).checkConnection())) {
                db.submitNewUserData(userName, userEmail, userPhone, year, branch, userCollege, userDiv, userRoll);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "Welcome to Tatva Moksh Lakshya", Toast.LENGTH_LONG).show();
                    }
                });

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "Please check your internet connection..", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }
    }
}