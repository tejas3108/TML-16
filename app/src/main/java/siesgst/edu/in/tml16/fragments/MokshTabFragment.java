package siesgst.edu.in.tml16.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import siesgst.edu.in.tml16.R;
import siesgst.edu.in.tml16.adapters.ViewPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class MokshTabFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager;

    public MokshTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_moksh_tab, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new EntertainmentFragment(), "Entertainment");
        adapter.addFragment(new ExtravaganzaFragment(), "Extravaganza");
        adapter.addFragment(new MusicanaFragment(), "Musicana");
        adapter.addFragment(new ArtFragment(), "Art");
        adapter.addFragment(new EDCFragment(), "EDC");
        viewPager.setAdapter(adapter);
    }
}
