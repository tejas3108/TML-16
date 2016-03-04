package siesgst.edu.in.tml16.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;

import siesgst.edu.in.tml16.R;
import siesgst.edu.in.tml16.TMLApplication;
import siesgst.edu.in.tml16.adapters.SponsorsAdapter;
import siesgst.edu.in.tml16.utils.ConnectionUtils;
import siesgst.edu.in.tml16.utils.DataHandler;
import siesgst.edu.in.tml16.utils.LocalDBHandler;
import siesgst.edu.in.tml16.utils.OnlineDBDownloader;

/**
 * A simple {@link Fragment} subclass.
 */
public class SponsorsFragment extends Fragment {

    RecyclerView recyclerView;

    CoordinatorLayout layout;
    SwipeRefreshLayout swipeRefreshLayout;

    SponsorsAdapter sponsorsAdapter;

    Tracker mTracker;
    public SponsorsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sponsors, container, false);

        layout = (CoordinatorLayout) view.findViewById(R.id.events_layout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_view);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshData();
            }
        });

        onRefreshData();

        TMLApplication application = (TMLApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        mTracker.setScreenName("Sponsors");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        return view;
    }

    public void onRefreshData() {
        swipeRefreshLayout.setRefreshing(true);
        new SponsorDataDownload().execute();
    }

    private class SponsorDataDownload extends AsyncTask<Void, Void, Void> {
        JSONArray array;
        SharedPreferences sharedPreferences;

        @Override
        protected void onPreExecute() {
            sharedPreferences = getActivity().getSharedPreferences("TML", Context.MODE_PRIVATE);
            if (new ConnectionUtils(getActivity()).checkConnection()) {
                new LocalDBHandler(getActivity()).dropSponsorsTable();
            } else {
                Snackbar.make(layout, "Can't connect to network..", Snackbar.LENGTH_INDEFINITE).setAction("Try Again", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRefreshData();
                    }
                }).show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            OnlineDBDownloader downloader = new OnlineDBDownloader(getActivity());
            try {
                downloader.downloadData();
            } catch (NullPointerException e) {

            }
            array = downloader.getSponsorsJSON();
            if (!sharedPreferences.getString("nw_status", "").equals("bad")) {
                new DataHandler(getActivity()).pushSponsorData(array);
            } else {
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Check your internet connection...", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (NullPointerException e) {

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            sponsorsAdapter = new SponsorsAdapter(getActivity());
            recyclerView.setAdapter(sponsorsAdapter);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mTracker.setScreenName("Sponsors");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

}
