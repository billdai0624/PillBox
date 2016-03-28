package kro.pillbox;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;

import java.util.Calendar;

/**
 * Created by user on 2016/3/17.
 */
public class NewDrug extends AppCompatActivity {
    private ImageButton pic;
    private EditText drugName;
    private EditText amount;
    private CheckBox morning, noon, evening, sleep;
    private Record_DatabaseHelper DBhelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_drug);
        pic = (ImageButton) findViewById(R.id.pic_upload);
        drugName = (EditText) findViewById(R.id.newDname);
        amount = (EditText) findViewById(R.id.newDamount);
        morning = (CheckBox) findViewById(R.id.newDrug_morning);
        noon = (CheckBox) findViewById(R.id.newDrug_noon);
        evening = (CheckBox) findViewById(R.id.newDrug_evening);
        sleep = (CheckBox) findViewById(R.id.newDrug_sleep);
        DBhelper = new Record_DatabaseHelper(this);
        db = DBhelper.getWritableDatabase();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_drug_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.finish) {
            String drug_name = drugName.getText().toString();
            int Amount = Integer.valueOf(amount.getText().toString());
            boolean isMorning = morning.isChecked();
            boolean isNoon = noon.isChecked();
            boolean isEvening = evening.isChecked();
            boolean isSleep = sleep.isChecked();

            ContentValues contentValues = new ContentValues();
            contentValues.put("drug_name", drug_name);
            contentValues.put("amount", Amount);
            contentValues.put("morning", isMorning);
            contentValues.put("noon", isNoon);
            contentValues.put("evening", isEvening);
            contentValues.put("sleep", isSleep);
            long id = db.insertOrThrow("record", null, contentValues);
            Intent it = new Intent();
            Bundle bd = new Bundle();
            bd.putString("drug_name", drug_name);
            bd.putInt("amount", Amount);
            bd.putBoolean("morning", isMorning);
            bd.putBoolean("noon", isNoon);
            bd.putBoolean("evening", isEvening);
            bd.putBoolean("sleep", isSleep);
            it.putExtras(bd);

            AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            Intent alarm = new Intent("Alarm");
            alarm.putExtra("drug_name", drug_name);
            alarm.putExtra("amount", Amount);
            alarm.putExtra("rowid", id);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            if (isMorning) {
                Calendar temp = Calendar.getInstance();
                temp.setTimeInMillis(System.currentTimeMillis());
                c.toString();
                temp.set(Calendar.HOUR_OF_DAY, 9);
                c.toString();
                temp.toString();
                if (temp.getTimeInMillis() < c.getTimeInMillis()) {
                    c.add(Calendar.DAY_OF_MONTH, 1);
                }
                c.set(Calendar.HOUR_OF_DAY, 9);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND,0);

                PendingIntent morning = PendingIntent.getBroadcast(this, 1, alarm, PendingIntent.FLAG_UPDATE_CURRENT);
                am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), morning);
            }
            if (isNoon) {
                Calendar temp = Calendar.getInstance();
                temp.setTimeInMillis(System.currentTimeMillis());
                temp.set(Calendar.HOUR_OF_DAY, 12);
                if (temp.getTimeInMillis() < c.getTimeInMillis()) {
                    c.add(Calendar.DAY_OF_MONTH, 1);
                }
                c.set(Calendar.HOUR_OF_DAY, 12);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND,0);
                PendingIntent noon = PendingIntent.getBroadcast(this, 2, alarm, PendingIntent.FLAG_UPDATE_CURRENT);
                am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), noon);
            }
            if (isEvening) {
                Calendar temp = Calendar.getInstance();
                temp.setTimeInMillis(System.currentTimeMillis());
                temp.set(Calendar.HOUR_OF_DAY, 18);
                if (temp.getTimeInMillis() < c.getTimeInMillis()) {
                    c.add(Calendar.DAY_OF_MONTH, 1);
                }
                c.set(Calendar.HOUR_OF_DAY, 18);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND,0);
                PendingIntent evening = PendingIntent.getBroadcast(this, 3, alarm, PendingIntent.FLAG_UPDATE_CURRENT);
                am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), evening);
            }
            if (isSleep) {
                Calendar temp = Calendar.getInstance();
                temp.setTimeInMillis(System.currentTimeMillis());
                temp.set(Calendar.HOUR_OF_DAY, 22);
                if (temp.getTimeInMillis() < c.getTimeInMillis()) {
                    c.add(Calendar.DAY_OF_MONTH, 1);
                }
                c.set(Calendar.HOUR_OF_DAY, 22);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND,0);
                PendingIntent sleep = PendingIntent.getBroadcast(this, 4, alarm, PendingIntent.FLAG_UPDATE_CURRENT);
                am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sleep);
            }

            db.close();
            DBhelper.close();
            setResult(RESULT_OK, it);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
