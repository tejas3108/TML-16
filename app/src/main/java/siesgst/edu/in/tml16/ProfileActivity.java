package siesgst.edu.in.tml16;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import siesgst.edu.in.tml16.utils.ConnectionUtils;
import siesgst.edu.in.tml16.utils.DataHandler;
import siesgst.edu.in.tml16.utils.LocalDBHandler;
import siesgst.edu.in.tml16.utils.OnlineDBDownloader;
import siesgst.edu.in.tml16.utils.QRInterface;

public class ProfileActivity extends PreferenceActivity {

    private ImageView mProfilepic;
    private TextView mUsername, mEmail, mPhone;

    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    SwipeRefreshLayout swipeRefreshLayout;

    PreferenceScreen preferenceScreen;
    PreferenceCategory preferenceCategory;

    Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);
        addPreferencesFromResource(R.xml.profile_preferences);

        sharedPreferences = getSharedPreferences("TML", MODE_PRIVATE);

        View profile = getLayoutInflater().inflate(R.layout.profile_card, null);

        mProfilepic = (ImageView) profile.findViewById(R.id.profile_pic1);
        if (!sharedPreferences.getString("profile_pic", "").equals("")) {
            Picasso.with(this).load(sharedPreferences.getString("profile_pic", "")).into(mProfilepic);
        } else {
            mProfilepic.setImageResource(R.mipmap.ic_launcher);
        }

        mUsername = (TextView) profile.findViewById(R.id.username);
        mUsername.setText(sharedPreferences.getString("username", ""));

        mEmail = (TextView) profile.findViewById(R.id.text_email);
        mEmail.setText(sharedPreferences.getString("email", ""));

        mPhone = (TextView) profile.findViewById(R.id.text_phone);
        mPhone.setText(sharedPreferences.getString("uPhone", ""));

        Preference regCode = findPreference("reg_code");
        regCode.setSummary("TML2016" + sharedPreferences.getString("uID", ""));
        regCode.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                launchQR();
                return false;
            }
        });

        Preference year = findPreference("year");
        year.setSummary(sharedPreferences.getString("uYear", ""));

        Preference branch = findPreference("branch");
        branch.setSummary(sharedPreferences.getString("uBranch", ""));

        Preference rollDiv = findPreference("roll_div");
        rollDiv.setSummary(sharedPreferences.getString("uRoll", "") + ", " + sharedPreferences.getString("uDivision", ""));

        Preference college = findPreference("college");
        college.setSummary(sharedPreferences.getString("uCollege", ""));

        preferenceCategory = (PreferenceCategory) (findPreference("event_perf"));
        preferenceScreen = getPreferenceScreen();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_view);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshData();
            }
        });

        onRefreshData();

        ListView listView = getListView();
        listView.addHeaderView(profile);

        TMLApplication application = (TMLApplication) getApplication();
        mTracker = application.getDefaultTracker();

        mTracker.setScreenName("Profile");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void launchQR() {
        final Dialog alertDialog = new Dialog(this);
        View qr = getLayoutInflater().inflate(R.layout.qr_code_layout, null);
        alertDialog.setContentView(qr);
        AppCompatButton qrButton = (AppCompatButton) alertDialog.findViewById(R.id.qr_code_button);
        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        QRInterface qrInterface = new QRInterface();
        ((ImageView) alertDialog.findViewById(R.id.qr_code_image)).setImageBitmap(qrInterface.encodeQRcode("TML2016_0001", 200, 200));
        alertDialog.show();
    }

    public void onRefreshData() {
        swipeRefreshLayout.setRefreshing(true);
        new AddProfileData().execute();
    }

    private class AddProfileData extends AsyncTask<Void, Void, Void> {

        JSONArray eventArray;
        SharedPreferences sharedPreferences;

        LocalDBHandler localDBHandler = new LocalDBHandler(ProfileActivity.this);

        @Override
        protected void onPreExecute() {
            if (new ConnectionUtils(ProfileActivity.this).checkConnection()) {
                localDBHandler.dropRegEventTable();
            } else {
                Toast.makeText(ProfileActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            final OnlineDBDownloader downloader = new OnlineDBDownloader(ProfileActivity.this);
            sharedPreferences = getSharedPreferences("TML", MODE_PRIVATE);
            editor = sharedPreferences.edit();
            if (new ConnectionUtils(ProfileActivity.this).checkConnection()) {
                downloader.sendUserEmail(sharedPreferences.getString("email", ""));
                eventArray = downloader.getRegEventDetailsArray();
                if (!String.valueOf(eventArray.length()).equals("0")) {
                    new DataHandler(ProfileActivity.this).pushRegEvents(eventArray);
                    editor.remove("null_array");
                    editor.putString("null_array", "no");
                    editor.apply();
                } else {
                    editor.remove("null_array");
                    editor.putString("null_array", "yes");
                    editor.apply();
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ProfileActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
            preferenceCategory.removeAll();
            if (!sharedPreferences.getString("null_array", "").equals("yes")) {
                for (int i = 0; i < localDBHandler.getRegEventData().size(); i = i + 2) {
                    Preference preferenceEvent = new Preference(ProfileActivity.this);
                    preferenceEvent.setTitle(localDBHandler.getRegEventData().get(i));
                    preferenceEvent.setSummary(localDBHandler.getRegEventData().get(i + 1));
                    preferenceCategory.addPreference(preferenceEvent);
                }
            } else {
                Preference preferenceEvent = new Preference(ProfileActivity.this);
                preferenceEvent.setSummary("You have not registered for any event.");
                preferenceCategory.addPreference(preferenceEvent);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mTracker.setScreenName("Profile");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
