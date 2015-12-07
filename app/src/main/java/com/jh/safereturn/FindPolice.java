package com.jh.safereturn;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;

/**
 * Created by HUNNY on 2015-11-30.
 */
public class FindPolice extends Fragment {
    EditText EditText2;
    Button findButton;
    View policeView;
    String slocal;
    TextView TextView2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseObject testObject = new ParseObject("PoliceDB");
    }

   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        policeView = inflater.inflate(R.layout.activity_findpolice, container,false);
        EditText2 = (EditText) policeView.findViewById(R.id.slocal);
        findButton = (Button) policeView.findViewById(R.id.find);
        TextView2 = (TextView) policeView.findViewById(R.id.mdata);

        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                slocal = EditText2.getText().toString();

                try {
                    ArrayList<ParseObject> datas = new ArrayList<ParseObject>(); // parse.com에서 읽어온 object들을 저장할 List
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("PoliceDB"); // 서버에 mydatas class 데이터 요청
                    query.whereEqualTo("slocal", slocal); // my_type이 1인 object만 읽어옴. 해당 함수 호출하지 않으면 class의 모든 데이터를 읽어옴.
                    datas.addAll(query.find()); // 읽어온 데이터를 List에 저장

                    // 읽어온 데이터를 화면에 보여주기 위한 처리
                    StringBuffer str = new StringBuffer();

                    for (ParseObject object : datas) {
                        str.append("◆");

                        str.append(" ");
                        str.append(object.get("local"));

                        str.append(" ");
                        str.append(object.get("slocal"));

                        str.append(" ");
                        str.append(object.get("road"));

                        str.append(" ");
                        str.append(object.get("pname"));
                        str.append(object.get("type"));
                        str.append(" \n ");

                        str.append("  ");
                        str.append("전화번호: ");
                        str.append(object.get("phonenumber"));
                        str.append("\n\n");
                    }

                    TextView2.setText(str.toString()); // TextView에 데이터를 넣어준다.
                    Linkify.addLinks(TextView2, Linkify.PHONE_NUMBERS);

                    datas.clear();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        return policeView;
    }
}

