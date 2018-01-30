package com.pdp.bkresv2.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pdp.bkresv2.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeviceFragment extends Fragment {


    public DeviceFragment() {
        // Required empty public constructor
    }

    public static DeviceFragment newInstance(String title){
        DeviceFragment df=new DeviceFragment();

        return df;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_device, container, false);
    }

}
