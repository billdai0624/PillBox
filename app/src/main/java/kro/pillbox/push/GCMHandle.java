package kro.pillbox.push;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Jacky on 2015/9/22.
 */
public class GCMHandle {

    AsyncTask<Void , Void , String> asyncTask ;
    final String GCM_VALUE = "gcm register" , GOOGLE_PROJECT_ID = "579411090555";
    Context context ;
    String regId ;
    GoogleCloudMessaging gcm ;
    SharedPreferenceControl sp ;
    AtomicInteger atomicId = new AtomicInteger();


    GCMHandle(Context context){
        this.context = context;
        sp = new SharedPreferenceControl(context);
        gcm = GoogleCloudMessaging.getInstance(context);
    }

    public void registerGCM(){
        int curr = currVersion();
        System.out.println(sp.getAppVersion(sp.AppVersion));
        if(sp.getAppVersion(sp.AppVersion)!= curr) {
            registerInBackground();
            Toast.makeText(context , "reg" , Toast.LENGTH_LONG).show();
        }else
            Toast.makeText(context , "reg exist" , Toast.LENGTH_LONG).show();
    }

    private int currVersion() {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }


    private void registerInBackground() {
        new AsyncTask<Void , Void ,  String>(){
            @Override
            protected String doInBackground(Void... params) {

                if(gcm == null)
                    gcm = GoogleCloudMessaging.getInstance(context);

                try {
                    regId = gcm.register(GOOGLE_PROJECT_ID);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                sp.storeRegisterId(GCM_VALUE , regId);
                sp.storeAppVersion(sp.AppVersion, currVersion());

                return regId ;
            }

            @Override
            protected void onPostExecute(String registerId) {
                Toast.makeText(context , "gcm Id  :  " + registerId , Toast.LENGTH_LONG).show();
            }
        }.execute(null , null , null);
    }

    public String getRegId(){
        return sp.getRegisterId(GCM_VALUE);
    }

    public void sendMessage(final String key , final String msg , final String phoneNum , final String userName , final String fileName){

        asyncTask = new AsyncTask<Void , Void ,  String>(){
            @Override
            protected String doInBackground(Void... params) {

                Bundle bundle = new Bundle();
                bundle.putString("Action" , key);
                bundle.putString("Message" , msg);
                bundle.putString("Phone Number" , phoneNum);
                bundle.putString("User Name" , userName);
                bundle.putString("File Name" , fileName);

                String id = Integer.toString(atomicId.incrementAndGet());

                try {
                    gcm.send( GOOGLE_PROJECT_ID + "@gcm.googleapis.com" , id , bundle);
                }catch (IOException ex){
                    ex.printStackTrace();
                }


                return "Send Message" ;
            }

            @Override
            protected void onPostExecute(String msg) {
                asyncTask = null ;
                Toast.makeText(context , msg , Toast.LENGTH_SHORT).show();
            }
        };
        asyncTask.execute(null, null , null);

    }

}
