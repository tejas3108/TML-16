package siesgst.edu.in.tml16;

import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;


public class IntroActivity extends AppIntro2 {

    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(AppIntroFragment.newInstance("Google Plus Login", "Know yourself better in this TML by having a customized profile.", R.mipmap.gplus, Color.BLACK));
        addSlide(AppIntroFragment.newInstance("Stay updated", "Follow posts about TML 2016 and stay updated", R.mipmap.news, Color.BLACK));
        addSlide(AppIntroFragment.newInstance("Incredible events", "TML, harbinger of wild times has something for everyone.", R.mipmap.events, Color.BLACK));
        addSlide(AppIntroFragment.newInstance("Invite everyone to The Great Indian Fair", "Help your friends to register for various events", R.mipmap.register_friend, Color.BLACK));
        addSlide(AppIntroFragment.newInstance("Unique Identity", "Earn yourself an unique ID by sigining in once. Use this ID throughout TML to register for various events.", R.mipmap.regcode, Color.BLACK));
        addSlide(AppIntroFragment.newInstance("Know your registered events", "Personalized list of registered events in your profile with their payment status.", R.mipmap.reg_events, Color.BLACK));
        addSlide(AppIntroFragment.newInstance("Smart Register", "Avoid long queues at the registration desk by registering for an event in a single tap.", R.mipmap.smart, Color.BLACK));
        addSlide(AppIntroFragment.newInstance("Quick Refresh", "Refresh to stay updated with the latest news and event details. Swipe down anywhere to refresh.", R.mipmap.refresh, Color.BLACK));

        showStatusBar(false);
        setFlowAnimation();
    }

    @Override
    public void onNextPressed() {
        // Do something when users tap on Next button.
    }

    @Override
    public void onDonePressed() {
        // Do something when users tap on Done button.
        finish();
    }

    @Override
    public void onSlideChanged() {
        // Do something when slide is changed
    }
}
