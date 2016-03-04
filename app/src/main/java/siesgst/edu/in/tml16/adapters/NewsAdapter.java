package siesgst.edu.in.tml16.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import siesgst.edu.in.tml16.R;
import siesgst.edu.in.tml16.helpers.FeedNews;
import siesgst.edu.in.tml16.utils.LocalDBHandler;

/**
 * Created by vishal on 3/2/16.
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    List<FeedNews> feedNewsList;
    Context context;
    FeedNews feedNews;
    int lastPosition;

    public NewsAdapter(Context context) {
        feedNewsList = new ArrayList<>();
        this.context = context;
        new LoadAdapterData().execute();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_layout, null);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {

        final FeedNews feedNews = feedNewsList.get(i);
        try {
            viewHolder.postIcon.setVisibility(View.VISIBLE);
            Picasso.with(context).load(feedNews.getPostImage()).placeholder(R.drawable.facebook).into(viewHolder.postIcon);
        } catch (IllegalArgumentException e) {
            viewHolder.postIcon.setVisibility(View.GONE);
            //Picasso.with(context).load(R.mipmap.ic_launcher).into(viewHolder.postIcon);
        }

        if(!feedNews.getPostMessage().isEmpty()) {
            viewHolder.postMessage.setVisibility(View.VISIBLE);
            viewHolder.postMessage.setText(feedNews.getPostMessage());
        } else {
            viewHolder.postMessage.setVisibility(View.GONE);
        }

        if (!feedNews.getPostLink().isEmpty()) {
            viewHolder.postLink = feedNews.getPostLink();
        } else {
            viewHolder.postLink = "https://www.facebook.com/siesgst.TML/";
        }
        viewHolder.readMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readMore(viewHolder.postLink);
            }
        });
        viewHolder.likes.setText(feedNews.getLikes());
        viewHolder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readMore(viewHolder.postLink);
            }
        });
        viewHolder.comments.setText(feedNews.getComments());
        viewHolder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readMore(viewHolder.postLink);
            }
        });
        viewHolder.iconLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readMore(viewHolder.postLink);
            }
        });
        viewHolder.iconComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readMore(viewHolder.postLink);
            }
        });
        viewHolder.postIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readMore(viewHolder.postLink);
            }
        });
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readMore(viewHolder.postLink);
            }
        });

        Animation animation = AnimationUtils.loadAnimation(context,
                (i > lastPosition) ? R.anim.up_from_bottom
                        : R.anim.down_from_up);
        viewHolder.itemView.setAnimation(animation);
        lastPosition = i;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        protected ImageView postIcon;
        protected TextView postMessage;
        protected String postLink;
        protected AppCompatButton readMore;
        protected TextView likes;
        protected TextView comments;
        protected ImageView iconLikes;
        protected ImageView iconComments;
        protected CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);

            postIcon = (ImageView) itemView.findViewById(R.id.news_image);
            postMessage = (TextView) itemView.findViewById(R.id.news_title);
            postMessage.setSelected(true);

            readMore = (AppCompatButton) itemView.findViewById(R.id.read_more);
            likes = (TextView) itemView.findViewById(R.id.likes);
            comments = (TextView) itemView.findViewById(R.id.comments);
            iconLikes = (ImageView) itemView.findViewById(R.id.icon_likes);
            iconComments = (ImageView) itemView.findViewById(R.id.icon_comments);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
        }
    }

    public void readMore(String postLink) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=" + postLink));
            context.startActivity(intent);
        } catch (Exception e) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(postLink));
            context.startActivity(intent);
        }
    }

    @Override
    public int getItemCount() {
        return feedNewsList.size();
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
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
                for (int i = 0; i < (localDBHandler.getFBData().size()) - 4; i = i + 5) {
                    feedNews = new FeedNews();
                    feedNews.setPostMessage(localDBHandler.getFBData().get(i));
                    feedNews.setPostImage(localDBHandler.getFBData().get(i + 1));
                    feedNews.setPostLink(localDBHandler.getFBData().get(i + 2));
                    feedNews.setNoOfLikes(localDBHandler.getFBData().get(i + 3));
                    feedNews.setNoOfComments(localDBHandler.getFBData().get(i + 4));
                    feedNewsList.add(feedNews);
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
