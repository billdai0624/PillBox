package kro.pillbox.push;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Jacky on 2015/11/28.
 */
public class AlarmManagerSet {

    SharedPreferenceControl sp ;
    boolean[] alarmOpen = new boolean[4];
    int[] minuteArray = new int[4];
    int[] hourArray = new int[4];
    Context context;
    int i = 1;

    AlarmManagerSet(Context context){
        sp = new SharedPreferenceControl(context);
        this.context = context;
    }

    public void setAlarm(){
        String[] hintArray = {"Morning", "Noon", "Night", "Sleep"};
        for(int i = 0 ; i < 4 ; i ++){
            HashMap<String , Object> hashMap = sp.getAlarmSetting(hintArray[i])  ;
            alarmOpen[i] = (boolean)hashMap.get("Switch");
            String[] tempArr = ( (String)hashMap.get("Time") ).split(" : ");
            hourArray[i] = Integer.parseInt(tempArr[0]);
            minuteArray[i] = Integer.parseInt(tempArr[1]);
        }

        for(int i = 0 ; i < sp.getDays() ; i++){
            for(int j = 0 ; j < 4 ; j++){
                if(alarmOpen[j]){
                    registerAlarmManager(i , hourArray[j] , minuteArray[j]);
                    registerAlarmManager2(i, hourArray[j], minuteArray[j]);
                }
            }
        }
    }

    private void registerAlarmManager(int days , int hours , int minutes){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, days);
        c.set(Calendar.HOUR_OF_DAY, hours);
        c.set(Calendar.MINUTE, minutes);
        c.set(Calendar.SECOND, 0);
        Intent intent = new Intent(context , AlarmReceiver.class);
        intent.putExtra("Flag" , "original");
        intent.putExtra("Time in  int" ,  c.getTimeInMillis());
        intent.putExtra("setting time" , hours + " : " + minutes);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context , i , intent , PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP , c.getTimeInMillis() , pendingIntent);
        i++;
    }

    private void registerAlarmManager2(int days , int hours , int minutes){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, days);
        c.set(Calendar.HOUR_OF_DAY, hours);
        c.set(Calendar.MINUTE, minutes + 30);
        c.set(Calendar.SECOND, 0);
        Intent intent = new Intent(context , AlarmReceiver.class);
        intent.putExtra("Flag" , "30Minutes");
        intent.putExtra("Time in  int" ,  c.getTimeInMillis());
        intent.putExtra("setting time" , hours + " : " + minutes);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context , i , intent , PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP , c.getTimeInMillis() , pendingIntent);
        i++;
    }

}
