package kro.pillbox.push;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by Jacky on 2015/9/11.
 */
public class SharedPreferenceControl {

    final String MEDIA_NUM = "get media number", VIDEO_NUM = "get video number",
            IMG_NUM = "get image number", SOUND_NUM = "get sound number", GCM = "get Gcm ",
            GCM_VALUE = "gcm register", DIR = "get dir ", IMG_DIR = "get img file dir", AppVersion = "App version",
            RELATION_LIST = "get list", RELATION_USER = "get user num", RELATION_INVITE = "invite num";
    Context context;

    SharedPreferenceControl(Context context) {
        this.context = context;
    }

    public void storeMediaNum(String key, int num) {
        SharedPreferences sp = context.getSharedPreferences(MEDIA_NUM, Context.MODE_PRIVATE);
        sp.edit().putInt(key, num).apply();
    }

    public int readMediaNum(String key) {
        SharedPreferences sp = context.getSharedPreferences(MEDIA_NUM, Context.MODE_PRIVATE);
        return sp.getInt(key, 1);
    }

    public void storeRegisterId(String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(GCM, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }

    public String getRegisterId(String key) {
        SharedPreferences sp = context.getSharedPreferences(GCM, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    public String getDir(String key) {
        SharedPreferences sp = context.getSharedPreferences(DIR, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    public void storeDir(String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(DIR, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }

    public int getAppVersion(String key) {
        SharedPreferences sp = context.getSharedPreferences(AppVersion, Context.MODE_PRIVATE);
        return sp.getInt(key, 0);
    }

    public void storeAppVersion(String key, int num) {
        SharedPreferences sp = context.getSharedPreferences(AppVersion, Context.MODE_PRIVATE);
        sp.edit().putInt(key, num).apply();
    }

    public String getUserInform(String key) {
        SharedPreferences sp = context.getSharedPreferences("User Information", Context.MODE_PRIVATE);
        return sp.getString(key, null);
    }

    public void storeUserInform(String key, String inform) {
        SharedPreferences sp = context.getSharedPreferences("User Information", Context.MODE_PRIVATE);
        sp.edit().putString(key, inform).apply();
    }

    public boolean saveRelationArray(String[] phoneNum, String[] userName, String preferenceKey) {
        SharedPreferences sp = context.getSharedPreferences(preferenceKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("relateList_size", phoneNum.length);
        for (int i = 0; i < phoneNum.length; i++) {
            editor.putString("PhoneNumber_" + i, phoneNum[i]);
            editor.putString("UserName_" + i, userName[i]);
        }
        return editor.commit();
    }

    public String[] getPhoneArray(String preferenceKey) {
        SharedPreferences sp = context.getSharedPreferences(preferenceKey, Context.MODE_PRIVATE);
        int size = sp.getInt("relateList_size", 0);
        String[] phoneNum = new String[size];
        for (int i = 0; i < size; i++) {
            phoneNum[i] = sp.getString("PhoneNumber_" + i, null);
        }

        return phoneNum;
    }

    public String[] getNameArray(String preferenceKey) {
        SharedPreferences sp = context.getSharedPreferences(preferenceKey, Context.MODE_PRIVATE);
        int size = sp.getInt("relateList_size", 0);
        String[] userName = new String[size];
        for (int i = 0; i < size; i++) {
            userName[i] = sp.getString("UserName_" + i, null);
        }

        return userName;
    }

    public boolean storeListInformation(HashMap<String, Object> hashMap) {
        SharedPreferences sp = context.getSharedPreferences("List Information", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String partOfKey = hashMap.get("Phone Number").toString();
        editor.putString(partOfKey + "_01", partOfKey);
        editor.putString(partOfKey + "_02", hashMap.get("User Name").toString());
        editor.putInt(partOfKey + "_03", (int) hashMap.get("Image"));
        editor.putBoolean(partOfKey + "_04", (boolean) hashMap.get("Switch"));
        editor.putBoolean(partOfKey + "_05", (boolean) hashMap.get("Check"));
        return editor.commit();
    }

    public HashMap<String, Object> getListInformation(String PhoneNumber) {
        SharedPreferences sp = context.getSharedPreferences("List Information", Context.MODE_PRIVATE);
        HashMap<String, Object> output = new HashMap<String, Object>();
        output.put("Phone Number", sp.getString(PhoneNumber + "_01", null));
        output.put("User Name", sp.getString(PhoneNumber + "_02", null));
        output.put("Image", sp.getInt(PhoneNumber + "_03", 0));
        output.put("Switch", sp.getBoolean(PhoneNumber + "_04", false));
        output.put("Check", sp.getBoolean(PhoneNumber + "_05", false));

        return output;
    }

    public boolean storeAlarmSetting(HashMap<String, Object> hashMap) {
        SharedPreferences sp = context.getSharedPreferences("Alarm setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String partOfKey = hashMap.get("Name").toString();
        editor.putString(partOfKey + "_01", partOfKey);
        editor.putString(partOfKey + "_02", (String) hashMap.get("Time"));
        editor.putInt(partOfKey + "_03", (int) hashMap.get("Image"));
        editor.putBoolean(partOfKey + "_04", (boolean) hashMap.get("Switch"));
        return editor.commit();
    }

    public HashMap<String, Object> getAlarmSetting(String key) {
        SharedPreferences sp = context.getSharedPreferences("Alarm setting", Context.MODE_PRIVATE);
        HashMap<String, Object> output = new HashMap<String, Object>();
        output.put("Name", sp.getString(key + "_01", null));
        output.put("Time", sp.getString(key + "_02", "00 : 00"));
        output.put("Image", sp.getInt(key + "_03", 0));
        output.put("Switch", sp.getBoolean(key + "_04", false));

        return output;
    }

    public boolean storeDays(int days){
        SharedPreferences sp = context.getSharedPreferences("Days", Context.MODE_PRIVATE);
        return sp.edit().putInt("Days" , days).commit();
    }

    public int getDays(){
        SharedPreferences sp = context.getSharedPreferences("Days", Context.MODE_PRIVATE);
        return sp.getInt("Days", 0);
    }

    public boolean storeClientMessage(HashMap<String , Object> hashMap){
        SharedPreferences sp = context.getSharedPreferences("Client Message", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String partOfKey = hashMap.get("Message Id").toString();
        editor.putString(partOfKey + "_01", partOfKey);
        editor.putString(partOfKey + "_02", (String) hashMap.get("File Name"));
        editor.putString(partOfKey + "_03", (String) hashMap.get("User Name"));
        editor.putString(partOfKey + "_04", (String) hashMap.get("Message"));
        editor.putBoolean(partOfKey + "_05", (boolean) hashMap.get("isRead"));
        return editor.commit();
    }

    public HashMap<String , Object> getClientMessage(int id){
        SharedPreferences sp = context.getSharedPreferences("Client Message", Context.MODE_PRIVATE);
        HashMap<String , Object> output = new HashMap<String, Object>();
        output.put("Message Id", sp.getString(id + "_01", null));
        output.put("File Name", sp.getString(id + "_02", null));
        output.put("User Name", sp.getString(id + "_03", null));
        output.put("Message", sp.getString(id + "_04", null));
        output.put("isRead", sp.getBoolean(id + "_05", false));
        return output;

    }

    public void storeMessageId(int id){
        SharedPreferences sp = context.getSharedPreferences("Message Id", Context.MODE_PRIVATE);
        sp.edit().putInt("Message Id" , id).apply();
    }

    public int getMessageId(){
        SharedPreferences sp = context.getSharedPreferences("Message Id", Context.MODE_PRIVATE);
        return sp.getInt("Message Id", 0);
    }

    public void storeTakeMedicineOrNot(boolean state){
        SharedPreferences sp = context.getSharedPreferences("Take medicine state", Context.MODE_PRIVATE);
        sp.edit().putBoolean("state" , state).apply();
    }

    public boolean getTakeMedicineOrNot(){
        SharedPreferences sp = context.getSharedPreferences("Take medicine state", Context.MODE_PRIVATE);
        return sp.getBoolean("state" , false);
    }

}
