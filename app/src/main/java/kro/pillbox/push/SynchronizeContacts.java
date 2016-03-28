package kro.pillbox.push;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jacky on 2015/11/22.
 */
public class SynchronizeContacts {

    String phoneString = "";
    String[] phoneArray ,  userName;
    SharedPreferenceControl sp;
    Context context ;

    final String SEVER_IP =  "http://163.21.245.122:8787/GCM-App-Server/Synchronize";

    SynchronizeContacts(Context context , String[] userName , String[] phoneArray){
        this.context = context ;
        this.userName = new String[phoneArray.length];
        this.phoneArray = new String[phoneArray.length];
        System.arraycopy(phoneArray , 0 , this.phoneArray , 0 , phoneArray.length);
        System.arraycopy(userName , 0 , this.userName , 0 , phoneArray.length);
    }

    public void combineArray(String[] targetArray){
        for(int i = 0 ; i < targetArray.length ; i++){
            if(targetArray[i] == null || targetArray[i].isEmpty()){
                break;
            }
            phoneString = phoneString.concat(" " + targetArray[i]);
        }
    }

    public void sendToServlet(){
        new AsyncTask<Void , Void , String>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String msg) {
                phoneString = msg.trim();
                Log.d("onPostExecute", msg);
                arrayModify();
            }

            @Override
            protected String doInBackground(Void... params) {
                URL url = null;
                HttpURLConnection httpURLConnection = null ;
                try {
                    byte[] myData = phoneString.getBytes();
                    url = new URL(SEVER_IP);
                    httpURLConnection =(HttpURLConnection)url.openConnection();
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                    httpURLConnection.setRequestProperty("Content-Length", String.valueOf(myData.length));
                    httpURLConnection.setRequestMethod("POST");

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    outputStream.write(myData, 0, myData.length);
                    outputStream.flush();
                    outputStream.close();

                    int responseCode = httpURLConnection.getResponseCode();
                    if (responseCode == 200) {
                        return getRecall(httpURLConnection.getInputStream());
                    }

                } catch (IOException ex){
                    ex.printStackTrace();
                }

                return "not";
            }
        }.execute(null, null, null);

    }

    private String getRecall(InputStream inputStream){
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] data = new byte[1024*1024];
        int count = 0;
        String result = "";
        if(inputStream != null) {

            try {
                while ((count = inputStream.read(data)) != -1) {
                    output.write(data , 0 ,count);
                }
                result = new String(output.toByteArray() , "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public void arrayModify(){
        sp = new SharedPreferenceControl(context);
        String[] newPhoneArray = phoneString.split(" ");
        String[] newUserArray = new String[newPhoneArray.length] ;

        for(int i = 0 ; i < newPhoneArray.length ; i++){
            newPhoneArray[i] = newPhoneArray[i].trim();
            for(int j = 0 ; j < phoneArray.length ; j++){
                if(newPhoneArray[i].equals(phoneArray[j])){
                    newUserArray[i] = userName[j];
                    Log.d("test owner name" , newUserArray[i]);
                }
            }
        }

        if(newPhoneArray.length != 0 && sp.saveRelationArray(newPhoneArray , newUserArray , "Relate_Array"))
            Log.d("share preference" , "success");

    }
}
