package com.jh.safereturn;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Jiyoung on 2015-12-03.
 */
public class FragmentHome extends Fragment {
    String phoneNo;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View homeView = inflater.inflate(R.layout.activity_home,container, false);
        return homeView;
    }
}
