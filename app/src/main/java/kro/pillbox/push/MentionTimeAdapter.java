package kro.pillbox.push;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.HashMap;

import kro.pillbox.R;

/**
 * Created by Jacky on 2015/11/27.
 */
public class MentionTimeAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    String[] hintArray = {"Morning", "Noon", "Night", "Sleep"};
    int[] imageArray = {R.drawable.icon_morning, R.drawable.icon_noon, R.drawable.icon_night, R.drawable.icon_sleep};
    Holder holder;
    SharedPreferenceControl sp;
    Context context;
    private TimePicker timePicker;

    public MentionTimeAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        sp = new SharedPreferenceControl(context);
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new Holder();
            convertView = inflater.inflate(R.layout.time_list_item, parent, false);
            holder.mentionTimeHint = (TextView) convertView.findViewById(R.id.mentionTimeHint);
            holder.timeDemo = (TextView) convertView.findViewById(R.id.timeDemo);
            holder.alarmSwitch = (Switch) convertView.findViewById(R.id.alarmSwitch);
            holder.timeImage = (ImageView) convertView.findViewById(R.id.timeImage);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.timeDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog(position);
            }
        });
        holder.alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    storeAlarmSetting(hintArray[position], getHashMapTime(hintArray[position], "Time"), imageArray[position], true);
                }
                else
                    storeAlarmSetting(hintArray[position], getHashMapTime(hintArray[position], "Time"), imageArray[position], false);
            }
        });
        holder.timeImage.setImageResource(imageArray[position]);
        holder.mentionTimeHint.setText(hintArray[position]);
        initial(hintArray[position]);
        return convertView;
    }

    private void customDialog(final int position) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_timepicker_dialog);
        timePicker = (TimePicker) dialog.findViewById(R.id.timePicker);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                int hourOfDay = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();
                String hour = hourOfDay > 9 ? hourOfDay + "" : "0" + hourOfDay;
                String minutes = minute > 9 ? minute + "" : "0" + minute;
                String output = hour + " : " + minutes;
                storeAlarmSetting(hintArray[position], output, imageArray[position], getHashMapBoolean(hintArray[position], "Switch"));
                holder.timeDemo.setText(output);
                notifyDataSetChanged();
            }
        });
        dialog.show();
    }


    private void storeAlarmSetting(String name, String time, int imageId, boolean isChecked) {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("Name", name);
        hashMap.put("Time", time);
        hashMap.put("Image", imageId);
        hashMap.put("Switch", isChecked);
        sp.storeAlarmSetting(hashMap);
    }

    private void initial(String key) {
        HashMap<String, Object> hashMap = sp.getAlarmSetting(key);
        holder.alarmSwitch.setChecked((Boolean) hashMap.get("Switch"));
        holder.timeDemo.setText((String) hashMap.get("Time"));
    }

    private String getHashMapTime(String name, String key) {
        HashMap<String, Object> hashMap = sp.getAlarmSetting(name);
        return (String) hashMap.get(key);
    }

    private boolean getHashMapBoolean(String name, String key) {
        HashMap<String, Object> hashMap = sp.getAlarmSetting(name);
        return (boolean) hashMap.get(key);
    }

    class Holder {
        TextView mentionTimeHint, timeDemo;
        Switch alarmSwitch;
        ImageView timeImage;
    }
}
