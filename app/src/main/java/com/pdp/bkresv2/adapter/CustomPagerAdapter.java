package com.pdp.bkresv2.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pdp.bkresv2.fragment.DeviceFragment;

import java.util.List;

/**
 * Created by vutuan on 31/01/2018.
 */

public class CustomPagerAdapter extends FragmentPagerAdapter {
    List<DeviceFragment> list;

    public CustomPagerAdapter(FragmentManager fm, List<DeviceFragment> mList) {
        super(fm);
        this.list=mList;
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
