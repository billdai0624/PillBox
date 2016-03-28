package kro.pillbox.push;

import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.Inflater;

import kro.pillbox.R;

/**
 * Created by Jacky on 2015/11/23.
 */
public class MentionTimeSetFragment extends Fragment {

    ArrayAdapter<String> dayAdapter  ;
    String[] day ;
    Spinner daySpinner ;
    ListView mentionTimeList;
    Button saveAlarmState;
    MentionTimeAdapter adapter ;
    SharedPreferenceControl sp;
    AlarmManagerSet am;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mention_time_set , container , false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sp = new SharedPreferenceControl(getActivity());
        am = new AlarmManagerSet(getActivity());
        mentionTimeList = (ListView)getActivity().findViewById(R.id.mentionTimeList);
        saveAlarmState = (Button)getActivity().findViewById(R.id.saveAlarmState);
        daySpinner = (Spinner)getActivity().findViewById(R.id.spinner);
        day = getResources().getStringArray(R.array.Days_list);
        dayAdapter = new ArrayAdapter<String>(getActivity() , android.R.layout.simple_spinner_item ,day);
        daySpinner.setAdapter(dayAdapter);
        daySpinner.setOnItemSelectedListener(onSelect);
        adapter = new MentionTimeAdapter(getActivity());
        mentionTimeList.setAdapter(adapter);
        saveAlarmState.setOnClickListener(onClick);

        int position;
        switch (sp.getDays()){
            case 1 : position= 0;break;
            case 2 : position= 1;break;
            case 3 : position= 2;break;
            case 5 : position= 3;break;
            case 10 : position= 4;break;
            default: position = 0 ;
        }

        daySpinner.setSelection(position);
    }


    private AdapterView.OnItemSelectedListener onSelect = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (position){
                case 0 : sp.storeDays(1);break;
                case 1 : sp.storeDays(2);break;
                case 2 : sp.storeDays(3);break;
                case 3 : sp.storeDays(5);break;
                case 4 : sp.storeDays(10);
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new AlarmManagerSet(getActivity()).setAlarm();
        }
    };
}
