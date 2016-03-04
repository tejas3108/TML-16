package siesgst.edu.in.tml16;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import siesgst.edu.in.tml16.utils.ConnectionUtils;
import siesgst.edu.in.tml16.utils.LocalDBHandler;
import siesgst.edu.in.tml16.utils.OnlineDBDownloader;

public class EventDetailsActivity extends AppCompatActivity {

    TextView description, eventDay, venue, head1, head2;
    ImageView iconHead1, iconHead2, detailImage;

    String eventName, image;

    ProgressDialog progressDialog;

    Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        eventName = getIntent().getExtras().getString("event_name");
        image = getIntent().getExtras().getString("type");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationDialog();
            }
        });

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(eventName);

        description = (TextView) findViewById(R.id.event_detail_detail);
        eventDay = (TextView) findViewById(R.id.event_detail_day);
        venue = (TextView) findViewById(R.id.event_detail_venue);
        head1 = (TextView) findViewById(R.id.event_detail_head1);
        head2 = (TextView) findViewById(R.id.event_detail_head2);
        iconHead1 = (ImageView) findViewById(R.id.icon_head1);
        iconHead2 = (ImageView) findViewById(R.id.icon_head2);
        detailImage = (ImageView) findViewById(R.id.detail_image);

        switch (image) {
            case "tatva":
                Picasso.with(this).load("http://tml.siesgst.ac.in/img/app/header/tatva.png").placeholder(R.drawable.drawer_logo).into(detailImage);
                break;
            case "moksh":
                Picasso.with(this).load("http://tml.siesgst.ac.in/img/app/header/moksh.png").placeholder(R.drawable.drawer_logo).into(detailImage);
                break;
            case "lakshya":
                Picasso.with(this).load("http://tml.siesgst.ac.in/img/app/header/lakshya.png").placeholder(R.drawable.drawer_logo).into(detailImage);
                break;
            default:
                detailImage.setImageResource(R.drawable.drawer_logo);
        }

        final ArrayList<String> details = (new LocalDBHandler(this)).getEventKaSabKuch(eventName);
        description.setText(details.get(0));
        eventDay.setText(details.get(1));
        venue.setText(details.get(2));
        head1.setText(details.get(3));
        head2.setText(details.get(4));

        iconHead1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + details.get(5)));
                startActivity(intent);
            }
        });

        iconHead2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + details.get(6)));
                startActivity(intent);
            }
        });

        TMLApplication application = (TMLApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    public void showConfirmationDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Are you sure you want to register?");
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog = ProgressDialog.show(EventDetailsActivity.this, "Registering", "Please wait...");
                new SmartRegister().execute();
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.create();
        alertDialog.show();
    }

    private class SmartRegister extends AsyncTask<Void, Void, Void> {

        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        OnlineDBDownloader onlineDBDownloader;

        @Override
        protected void onPreExecute() {
            sharedPreferences = getSharedPreferences("TML", MODE_PRIVATE);
            onlineDBDownloader = new OnlineDBDownloader(EventDetailsActivity.this);
        }

        @Override
        protected Void doInBackground(Void... params) {
            if ((new ConnectionUtils(EventDetailsActivity.this).checkConnection())) {
                if (!(sharedPreferences.getInt("login_status", 0) == 1)) {
                    onlineDBDownloader.submitRegData(sharedPreferences.getString("username", ""), sharedPreferences.getString("email", ""), sharedPreferences.getString("uPhone", ""), sharedPreferences.getString("uYear", ""), sharedPreferences.getString("uBranch", ""), sharedPreferences.getString("uCollege", ""), sharedPreferences.getString("uDivision", ""), sharedPreferences.getString("uRoll", ""), eventName);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(EventDetailsActivity.this, sharedPreferences.getString("reg_status", "") + " \nComplete your payment at the registration desk.", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Smart Register")
                                    .setAction("Register")
                                    .build());
                        }
                    });
            } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(EventDetailsActivity.this, "You need to sign in to register for this event.", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(EventDetailsActivity.this, "Please check your internet connection..", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}