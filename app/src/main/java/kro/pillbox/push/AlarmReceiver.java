package kro.pillbox.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;

import kro.pillbox.R;

/**
 * Created by Jacky on 2015/11/28.
 */
public class AlarmReceiver extends BroadcastReceiver {

    GCMHandle gcmHandle;
    SharedPreferenceControl sp;

    @Override
    public void onReceive(Context context, Intent intent) {
        String time = intent.getStringExtra("setting time");
        long timeInInt = intent.getLongExtra("Time in  int", -1);
        String flag = intent.getStringExtra("Flag");
        long currentTime = System.currentTimeMillis();
        sp = new SharedPreferenceControl(context);
        Log.d("get current time", currentTime + "" );
        if( (currentTime / 60000) <= (timeInInt / 60000)) {
            if (flag.equals("original")) {
                final int notifyID = 1;
                Intent newIntent = new Intent(context, NavigationDrawerControl.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, newIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                final Notification notification = new Notification.Builder(context).setSmallIcon(R.drawable.abc_ic_menu_paste_mtrl_am_alpha).
                        setContentTitle("Alarm Message").setContentText("Now , " + time + " .  It's  Time to take medicine").setContentIntent(pendingIntent)
                        .setDefaults(Notification.DEFAULT_VIBRATE).build();
                notificationManager.notify(notifyID, notification);
            } else if (flag.equals("30Minutes") && (!sp.getTakeMedicineOrNot())) {
                String[] UserName = sp.getNameArray("Relate_Array");
                String[] PhoneNumber = sp.getPhoneArray("Relate_Array");
                for (int i = 0; i < PhoneNumber.length; i++) {
                    HashMap<String, Object> hashMap = sp.getListInformation(PhoneNumber[i]);
                    if ((boolean) hashMap.get("Switch")) {
                        gcmHandle = new GCMHandle(context);
                        gcmHandle.sendMessage("C2C", sp.getUserInform("User name") + " forgot to take medicine", PhoneNumber[i], UserName[i], "");
                    }
                }

            } else if (sp.getTakeMedicineOrNot()) {
                sp.storeTakeMedicineOrNot(false);
                Log.d("test", "take medicine");
            }
        }


    }
}
