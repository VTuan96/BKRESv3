package com.pdp.bkresv2.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by vutuan on 08/03/2018.
 */

public class CustomPagerPagerGiamSat extends FragmentPagerAdapter {
    List<Fragment> list;

    public CustomPagerPagerGiamSat(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.list = list;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
