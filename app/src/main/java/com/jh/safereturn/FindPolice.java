package com.jh.safereturn;



import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import android.support.v4.app.Fragment;


import java.util.ArrayList;

/**
 * Created by HUNNY on 2015-11-30.
 */
public class FindPolice extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PoliceActivity policeActivity = ((PoliceActivity)getActivity());
        policeActivity.onCreate(savedInstanceState);

        return policeActivity.onCreateView(inflater, container, savedInstanceState);
    }

}


