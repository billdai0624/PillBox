package kro.pillbox.push;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import kro.pillbox.R;

/**
 * Created by Jacky on 2015/9/25.
 */
public class C2CMsgSendPage extends ActionBarActivity {

    TextView uploadState ;
    String targetFilePath , targetFileName , textMessage , userName;
    String[] phoneNum ;
    GCMHandle gcmHandle ;
    final String SEVER_IP  =  "http://163.21.245.122:8787/GCM-App-Server/uploadFile";
    final int BUFFER_SIZE = 1024 * 1024 *32 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mention_page);
        gcmHandle = new GCMHandle(getApplicationContext());
        Intent intent = getIntent();
        phoneNum = intent.getStringArrayExtra("Phone Number");
        userName = intent.getStringExtra("User Name");
        targetFilePath = intent.getStringExtra("Target file path");
        textMessage = intent.getStringExtra("Text message");
        targetFileName = intent.getStringExtra("Target file name");

        uploadState = (TextView)findViewById(R.id.uploadState);
        uploadFile();
    }

    private void uploadFile() {
        Log.d("file path ",targetFilePath);
        new AsyncTask<Void , Void , String>(){
            @Override
            protected String doInBackground(Void... params) {

                HttpURLConnection httpURLConnection = null;
                URL url;
                File file;
                FileInputStream fileInputStream ;
                try {

                    url = new URL(SEVER_IP);
                    file = new File(targetFilePath);
                    fileInputStream = new FileInputStream(file);
                    httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("filename", targetFileName);
                    httpURLConnection.connect();
                    System.out.println("start send");

                    DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int bufferLength = -1 ;

                    while (  (bufferLength = fileInputStream.read(buffer)) > 0) {
                        dataOutputStream.write(buffer, 0, bufferLength);
                        System.out.println("uploading" + bufferLength);
                    }


                    System.out.println("data was written");

                    dataOutputStream.flush();
                    dataOutputStream.close();
                    fileInputStream.close();

                    int responseCode = httpURLConnection.getResponseCode();
                    if (responseCode == 200) {
                        return "OK";
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }

                return "Fail";
            }


            @Override
            protected void onPostExecute(String msg) {
                if(msg.equals("OK"))
                    uploadState.setText("Upload Success");
                for(int i = 0 ; i < phoneNum.length ; i++) {
                    gcmHandle.sendMessage("C2C", textMessage, phoneNum[i], userName, targetFileName);
                }
            }


        }.execute(null , null , null);


    }



}
