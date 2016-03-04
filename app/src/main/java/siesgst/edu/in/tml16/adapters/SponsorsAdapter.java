package siesgst.edu.in.tml16.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import siesgst.edu.in.tml16.R;
import siesgst.edu.in.tml16.helpers.FeedSponsors;
import siesgst.edu.in.tml16.utils.LocalDBHandler;

/**
 * Created by vishal on 16/2/16.
 */
public class SponsorsAdapter extends RecyclerView.Adapter<SponsorsAdapter.ViewHolder> {

    List<FeedSponsors> feedSponsorsList;
    Context context;
    FeedSponsors feedSponsors;

    public SponsorsAdapter(Context context) {
        feedSponsorsList = new ArrayList<>();
        this.context = context;
        new LoadAdapterData().execute();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sponsors_layout, null);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final FeedSponsors feedSponsors = feedSponsorsList.get(i);

        String imageLink = feedSponsors.getSPath();
        Picasso.with(context).load("http://tml.siesgst.ac.in" + imageLink).placeholder(R.mipmap.refresh).into(viewHolder.sponsorImage);

        viewHolder.sponsorName.setText(feedSponsors.getSName());
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        protected ImageView sponsorImage;
        protected TextView sponsorName;
        protected AppCompatButton sponsorLink;

        public ViewHolder(View itemView) {
            super(itemView);

            sponsorImage = (ImageView) itemView.findViewById(R.id.sponsor_image);
            sponsorName = (TextView) itemView.findViewById(R.id.sponsor_name);

        }
    }

    @Override
    public int getItemCount() {
        return feedSponsorsList.size();
    }

    private class LoadAdapterData extends AsyncTask<Void, Void, Void> {
        LocalDBHandler localDBHandler;

        @Override
        protected void onPreExecute() {
            localDBHandler = new LocalDBHandler(context);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                for (int i = 0; i < (localDBHandler.getSponsorDetails().size()) - 2; i = i + 3) {
                    feedSponsors = new FeedSponsors();
                    feedSponsors.setSName(localDBHandler.getSponsorDetails().get(i));
                    feedSponsors.setSPath(localDBHandler.getSponsorDetails().get(i+1));
                    feedSponsors.setSLink(localDBHandler.getSponsorDetails().get(i+2));
                    feedSponsorsList.add(feedSponsors);
                }
            } catch (NullPointerException e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            notifyDataSetChanged();
        }
    }
}
