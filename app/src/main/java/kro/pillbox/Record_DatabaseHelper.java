package kro.pillbox;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 2016/3/17.
 */
public class Record_DatabaseHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "pillBox.db";
    private final static int DATABASE_VERSION = 1;
    public Record_DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE record(_Id INTEGER PRIMARY KEY AUTOINCREMENT, drug_name TEXT" +
                ", amount INTEGER, morning INTEGER, noon INTEGER, evening INTEGER, Sleep INTEGER)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
