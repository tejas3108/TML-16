package siesgst.edu.in.tml16.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import siesgst.edu.in.tml16.adapters.EventAdapter;
import siesgst.edu.in.tml16.utils.ConnectionUtils;
import siesgst.edu.in.tml16.utils.DataHandler;
import siesgst.edu.in.tml16.utils.LocalDBHandler;
import siesgst.edu.in.tml16.utils.OnlineDBDownloader;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class ServerCommunicationService extends IntentService {

    JSONArray array;
    JSONObject object;

    SharedPreferences sharedPreferences;

    public ServerCommunicationService() {
        super("ServerCommunicationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            downloadFBData();
            downloadEvents();
        }
    }

    public void downloadEvents() {
        if (new ConnectionUtils(getApplicationContext()).checkConnection()) {
            new LocalDBHandler(getApplicationContext()).dropEventsTable();
        }
        sharedPreferences = getApplicationContext().getSharedPreferences("TML", Context.MODE_PRIVATE);
        OnlineDBDownloader downloader = new OnlineDBDownloader(getApplicationContext());
        downloader.downloadData();
        array = downloader.getJSON();
        if (!sharedPreferences.getString("nw_status", "").equals("bad")) {
            new DataHandler(getApplicationContext()).decodeAndPushJSON(array);
            Log.d("TMLCommunicationService", "Service has pushed events");
        }
    }

    public void downloadFBData() {
        if (new ConnectionUtils(getApplicationContext()).checkConnection()) {
            new LocalDBHandler(getApplicationContext()).dropFBTable();
        }
        sharedPreferences = getApplicationContext().getSharedPreferences("TML", Context.MODE_PRIVATE);
        OnlineDBDownloader downloader = new OnlineDBDownloader(getApplicationContext());
        downloader.getFacebookData();
        object = downloader.getFBObject();

        if (!sharedPreferences.getString("nw_status", "").equals("bad")) {
            try {
                new DataHandler(getApplicationContext()).pushFBData(object);
            } catch (NullPointerException e) {

            }
            Log.d("TMLCommunicationService", "Service has pushed FB data");
        }

    }
}
