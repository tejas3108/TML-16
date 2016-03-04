package siesgst.edu.in.tml16;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class AboutActivity extends AppCompatActivity {

    ImageView imageVishal, imageShata, imageObla, imageNav, fbVishal, fbShata, fbObla, fbNav, instaVishal, instaShata, instaObla, instaNav, gVishal, gShata, gObla, gNav, gitVishal, gitShata, gitObla, gitNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        final TextView moreText = (TextView) findViewById(R.id.more);

        imageShata = (ImageView) findViewById(R.id.pic_shata);
        imageVishal = (ImageView) findViewById(R.id.pic_vishal);
        imageObla = (ImageView) findViewById(R.id.pic_obla);
        imageNav = (ImageView) findViewById(R.id.pic_nav);

        fbVishal = (ImageView) findViewById(R.id.fb);
        fbVishal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchBrowserView("https://www.facebook.com/vishal.dubey.39589");
            }
        });
        fbObla = (ImageView) findViewById(R.id.fb_obla);
        fbObla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchBrowserView("https://www.facebook.com/speedyos");
            }
        });
        fbShata = (ImageView) findViewById(R.id.fb_shata);
        fbShata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchBrowserView("https://www.facebook.com/shatacheruvu");
            }
        });
        fbNav = (ImageView) findViewById(R.id.fb_nav);
        fbNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchBrowserView("https://www.facebook.com/navpinder");
            }
        });

        instaVishal = (ImageView) findViewById(R.id.insta);
        instaVishal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchBrowserView("https://www.instagram.com/vishal_android_freak/");
            }
        });
        instaShata = (ImageView) findViewById(R.id.insta_shata);
        instaShata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchBrowserView("https://www.instagram.com/shatavarth/");
            }
        });
        instaObla = (ImageView) findViewById(R.id.insta_obla);
        instaObla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchBrowserView("https://www.instagram.com/thespeedyos/");
            }
        });
        instaNav = (ImageView) findViewById(R.id.insta_nav);
        instaNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchBrowserView("https://www.instagram.com/navpinder/");
            }
        });

        gVishal = (ImageView) findViewById(R.id.gplus);
        gVishal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchBrowserView("https://plus.google.com/u/0/+VishalDubeyvishal-android-freak");
            }
        });
        gShata = (ImageView) findViewById(R.id.gplus_shata);
        gShata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchBrowserView("https://plus.google.com/u/0/+ShataCheruvu");
            }
        });
        gObla = (ImageView) findViewById(R.id.gplus_obla);
        gObla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchBrowserView("https://plus.google.com/+SrinathSpeedyos");
            }
        });

        gitVishal = (ImageView) findViewById(R.id.git);
        gitVishal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchBrowserView("https://github.com/vishal-android-freak");
            }
        });
        gitObla = (ImageView) findViewById(R.id.git_obla);
        gitObla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchBrowserView("https://github.com/srinathos");
            }
        });
        gitShata = (ImageView) findViewById(R.id.git_shata);
        gitShata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchBrowserView("https://github.com/shatacheruvu");
            }
        });

        Picasso.with(this).load("http://tml.siesgst.ac.in/img/devs/shata.png").into(imageShata);
        Picasso.with(this).load("http://tml.siesgst.ac.in/img/devs/vishal.png").into(imageVishal);
        Picasso.with(this).load("http://tml.siesgst.ac.in/img/devs/obla.png").into(imageObla);
        Picasso.with(this).load("http://tml.siesgst.ac.in/img/devs/nav.png").into(imageNav);

        final AppCompatButton readMoreButton = (AppCompatButton) findViewById(R.id.read_more);
        readMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreText.setVisibility(View.VISIBLE);
                readMoreButton.setVisibility(View.GONE);
            }
        });
    }

    public void launchBrowserView (String link) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(AboutActivity.this, "No app installed that supports the following action.", Toast.LENGTH_SHORT).show();
        }
    }

}
