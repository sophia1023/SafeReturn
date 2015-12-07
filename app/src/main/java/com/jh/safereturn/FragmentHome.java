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
public class FragmentHome extends Fragment {


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View homeView = inflater.inflate(R.layout.activity_home,container, false);
        final MainActivity activity = ((MainActivity)getActivity());

        Button settingBtn = (Button)homeView.findViewById(R.id.setting);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.setting();
            }
        });
        return homeView;
    }

}
