package kro.pillbox;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Alarm_Receiver extends BroadcastReceiver {
    private Record_DatabaseHelper DBhelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private int amount;
    @Override
    public void onReceive(Context context, Intent intent) {
        DBhelper = new Record_DatabaseHelper(context);
        db = DBhelper.getWritableDatabase();
        cursor = db.rawQuery("Select amount from record where rowid=?", new String[]{Long.toString(intent.getLongExtra("rowid", 0))});
        while(cursor.moveToNext()){
            amount = cursor.getInt(0);
        }
        cursor.close();
        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(context).setContentTitle("確認服藥！")
                .setContentText("已偵測到服藥動作！ 「普拿疼」剩下3顆囉！").setSmallIcon(R.drawable.pill).build();
        nm.notify(1, notification);
        amount--;
        ContentValues cv = new ContentValues();
        cv.put("amount", amount);

        db.update("record", cv, "rowid=?", new String[]{Long.toString(intent.getLongExtra("rowid", 0))});
        db.close();
        DBhelper.close();
    }
}
