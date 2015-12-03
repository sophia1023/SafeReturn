package com.jh.safereturn;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Jiyoung on 2015-12-03.
 */
public class SirenMaker extends Fragment {

    Button sirenStartBtn, sirenStopBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View sirenView = inflater.inflate(R.layout.activity_siren, container,false);
        sirenStartBtn = (Button)sirenView.findViewById(R.id.sirenStartBtn);
        sirenStopBtn = (Button)sirenView.findViewById(R.id.sirenStopBtn);
        final MainActivity sensorActivity = ((MainActivity)getActivity());

        sirenStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sensorActivity.onStart();
            }

        });

        sirenStopBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                sensorActivity.onStop();
            }
        });

        return sirenView;
    }
}
