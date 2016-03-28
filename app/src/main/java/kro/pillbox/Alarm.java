package kro.pillbox;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by user on 2016/3/16.
 */
public class Alarm extends AppCompatActivity {
    ListView lv;
    ArrayList<Alarm_item> alarms;
    ArrayAdapter<Alarm_item> alarmAdapter;
    private Record_DatabaseHelper DBhelper;
    private SQLiteDatabase db;
    private Cursor cursor;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);
        lv = (ListView) this.findViewById(R.id.alarm);
        alarms = new ArrayList<>();
        DBhelper = new Record_DatabaseHelper(this);
        db = DBhelper.getReadableDatabase();
        cursor = db.rawQuery("Select drug_name, amount, morning, noon, evening, sleep from record where amount>? order by _Id DESC", new String[]{"0"});
        while (cursor.moveToNext()) {
            Alarm_item item = new Alarm_item(cursor.getString(0), cursor.getInt(1), (cursor.getInt(2) > 0), (cursor.getInt(3) > 0), (cursor.getInt(4) > 0), (cursor.getInt(5) > 0));
            alarms.add(item);
        }
        alarmAdapter = new ArrayAdapter<Alarm_item>(this, R.layout.alarm_item, alarms) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Alarm_item item = getItem(position);
                if (null == convertView) {
                    LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = li.inflate(R.layout.alarm_item, null);
                }
                ImageView pic = (ImageView) convertView.findViewById(R.id.alarm_pic);
                TextView drugName = (TextView) convertView.findViewById(R.id.alarm_Dname);
                TextView alarmAmount = (TextView) convertView.findViewById(R.id.alarm_amount);
                TextView morning = (TextView) convertView.findViewById(R.id.alarm_morning);
                TextView noon = (TextView) convertView.findViewById(R.id.alarm_noon);
                TextView evening = (TextView) convertView.findViewById(R.id.alarm_evening);
                TextView sleep = (TextView) convertView.findViewById(R.id.alarm_sleep);

                drugName.setText(item.getDrugName());
                alarmAmount.setText(Integer.toString(item.getAmount()));
                if (item.isMorning()) {
                    morning.setTextColor(Color.BLUE);
                } else {
                    morning.setTextColor(Color.GRAY);
                }
                if (item.isNoon()) {
                    noon.setTextColor(Color.BLUE);
                } else {
                    noon.setTextColor(Color.GRAY);
                }
                if (item.isEvening()) {
                    evening.setTextColor(Color.BLUE);
                } else {
                    evening.setTextColor(Color.GRAY);
                }
                if (item.isSleep()) {
                    sleep.setTextColor(Color.BLUE);
                } else {
                    sleep.setTextColor(Color.GRAY);
                }
                return convertView;
            }
        };
        lv.setAdapter(alarmAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.alarm_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (item.getItemId() == R.id.newAlarm) {
            Intent it = new Intent(this, NewDrug.class);
            startActivityForResult(it, 1);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Bundle bd = data.getExtras();
                Alarm_item item = new Alarm_item(bd.getString("drug_name"), bd.getInt("amount"), bd.getBoolean("morning"), bd.getBoolean("noon"), bd.getBoolean("evening"), bd.getBoolean("sleep"));
                alarms.add(0, item);
                alarmAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBhelper.close();
        db.close();
    }
}
