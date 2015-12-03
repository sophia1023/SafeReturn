package com.jh.safereturn;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Jiyoung on 2015-11-30.
 */
public class ShowLocation extends android.support.v4.app.Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View locationView = inflater.inflate(R.layout.activity_maps, container,false);
        final LocationSMS locationSMS  = ((LocationSMS)getActivity());
        return locationView;
    }
}
