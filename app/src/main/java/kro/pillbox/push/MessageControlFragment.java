package kro.pillbox.push;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.HashMap;

import kro.pillbox.R;

/**
 * Created by Jacky on 2015/11/28.
 */
public class MessageControlFragment extends Fragment {
    String[] hintArray = {"Morning", "Noon", "Night", "Sleep"};
    TextView textNoRead, textRead, takeMedicineHint;
    ImageView imageHint ;
    LinearLayout messageLayout1, messageLayout2;
    SharedPreferenceControl sp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.message_control_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sp = new SharedPreferenceControl(getActivity());
        textNoRead = (TextView) getActivity().findViewById(R.id.textNoRead);
        textRead = (TextView) getActivity().findViewById(R.id.textRead);
        takeMedicineHint = (TextView) getActivity().findViewById(R.id.takeMedicineHint);
        imageHint = (ImageView) getActivity().findViewById(R.id.imageHint);
        messageLayout1 = (LinearLayout) getActivity().findViewById(R.id.messageLayout1);
        messageLayout2 = (LinearLayout) getActivity().findViewById(R.id.messageLayout2);

        messageLayout1.setOnClickListener(onClick);
        messageLayout2.setOnClickListener(onClick);

        int totalCount = sp.getMessageId();
        int readCount = 0;
        for (int i = 1; i <= totalCount; i++) {
            HashMap<String, Object> hashMap = sp.getClientMessage(i);
            if ((boolean) hashMap.get("isRead")) {
                readCount++;
            }
        }
        textRead.setText((totalCount - readCount) + " new \nmessage");
        textNoRead.setText(readCount + " old \nmessage");
        alarmCheck();
    }

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.messageLayout1) {
                Intent intent = new Intent(getActivity(), MediaMessageList.class);
                intent.putExtra("Title hint", "New message");
                getActivity().startActivity(intent);

            } else {
                Intent intent = new Intent(getActivity(), MediaMessageList.class);
                intent.putExtra("Title hint", "Old message");
                getActivity().startActivity(intent);
            }

        }
    };

    private void alarmCheck() {
        long[] array = new long[8];
        boolean[] alarmState = new boolean[4];
        for (int i = 0; i < 4; i++) {
            HashMap<String, Object> hashMap = sp.getAlarmSetting(hintArray[i]);
            String[] sTime = ((String) hashMap.get("Time")).split(" : ");
            alarmState[i] = (boolean)hashMap.get("Switch");
            int minutes = Integer.parseInt(sTime[1]);
            int hours = Integer.parseInt(sTime[0]);
            Calendar c1 = Calendar.getInstance();
            c1.add(Calendar.DATE, 0);
            c1.set(Calendar.HOUR_OF_DAY, hours);
            c1.set(Calendar.MINUTE, minutes);
            array[i * 2] = c1.getTimeInMillis();

            Calendar c2 = Calendar.getInstance();
            c2.add(Calendar.DATE, 0);
            c2.set(Calendar.HOUR_OF_DAY, hours);
            c2.set(Calendar.MINUTE, minutes + 30);
            array[1 + i * 2] = c2.getTimeInMillis();
        }
        long currentTime = System.currentTimeMillis();

        if ((currentTime >= array[0] && currentTime <= array[1] && alarmState[0])
                || (currentTime >= array[2] && currentTime <= array[3] && alarmState[1])
                || (currentTime >= array[4] && currentTime <= array[5] && alarmState[2])
                || (currentTime >= array[6] && currentTime <= array[7] && alarmState[3]) )
        {
            takeMedicineHint.setText("Time to take medicine");
            imageHint.setImageResource(R.drawable.image_takemedicine);
        } else {
            takeMedicineHint.setText("Today is a good day");
        }

    }
}
