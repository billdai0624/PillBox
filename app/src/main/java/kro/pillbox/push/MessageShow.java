package kro.pillbox.push;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import kro.pillbox.R;


/**
 * Created by Jacky on 2015/9/29.
 */
public class MessageShow extends ActionBarActivity{

    RelativeLayout fragmentShow;
    TextView inviteNumShow , msgShow , hintText1 , progressDemo;
    String filename = "" , dir = "", serverMsg = "", userName = "";
    FileDeal fileDeal;
    FragmentTransaction fragmentTransaction;
    SharedPreferenceControl sp ;
    VideoPlaybackFragment videoFrag;
    AudioPlaybackFragment audioFrag ;
    DefaultFileFragment fileFragment;
    ImageSetFragment imageFragment;
    private final String SEVER_IP  = "http://163.21.245.122:8787/GCM-App-Server/downloadFile";
    final int BUFFER_SIZE = 1024 * 1024 *32 ;
    int index ;
    Button downloadYes , downloadNo ;
    ProgressBar downloadProgress ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_show);
        sp = new SharedPreferenceControl(this);

        Intent intent = getIntent();
        serverMsg = intent.getStringExtra("Message");
        filename = intent.getStringExtra("File Name");
        userName = intent.getStringExtra("User Name");
        index = intent.getIntExtra("Index" , 0);

        HashMap<String , Object> hashMap = new HashMap<String, Object>();
        hashMap.put("Message Id", index + "");
        hashMap.put("File Name", filename );
        hashMap.put("User Name" ,userName );
        hashMap.put("Message" ,serverMsg );
        hashMap.put("isRead" , true);
        sp.storeClientMessage(hashMap);
        Log.d("test" , "" + index);

        downloadYes = (Button)findViewById(R.id.downloadYes);
        downloadNo = (Button)findViewById(R.id.downloadNo);
        downloadYes.setOnClickListener(onclick);
        downloadNo.setOnClickListener(onclick);
        fragmentShow = (RelativeLayout)findViewById(R.id.fragmentShow);
        downloadProgress = (ProgressBar)findViewById(R.id.downloadProgress);
        inviteNumShow = (TextView)findViewById(R.id.inviteNumShow);
        hintText1 = (TextView)findViewById(R.id.hintText1);
        msgShow = (TextView)findViewById(R.id.msgShow);
        progressDemo = (TextView)findViewById(R.id.progressDemo);

        msgShow.setText(serverMsg);
        inviteNumShow.setText("From : " + userName);
        generateStoreDir();
        dir = fileDeal.getDir("Download");
    }


    private void generateStoreDir() {

        fileDeal = new FileDeal(this);
        if(!fileDeal.isDirExist("Root")){
            if(fileDeal.makeFolder("Root")) {
                if(fileDeal.makeFolder("Download"))
                    Toast.makeText(MessageShow.this, "mkdir success", Toast.LENGTH_LONG).show();
            }
        }
        else {
            if(!fileDeal.isDirExist("Download")){
                if(fileDeal.makeFolder("Download")){
                    Toast.makeText(MessageShow.this, "mkdir success", Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(MessageShow.this, "file exist", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void start() {

        new AsyncTask<Void , Integer , String>() {
            @Override
            protected String doInBackground(Void... params) {

                HttpURLConnection httpURLConnection = null;
                try{
                    URL url = new URL(SEVER_IP);
                    httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setRequestProperty("filename", filename);
                    httpURLConnection.connect();
                    int fileSize = httpURLConnection.getContentLength();
                    downloadProgress.setMax(fileSize);


                    File file = new File(dir + filename);
                    Log.d("Download", "file path" + dir + filename);
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    InputStream inputStream = httpURLConnection.getInputStream();

                    byte[] bytes = new byte[BUFFER_SIZE];

                    int length = -1;
                    int downloadCount = -1 ;

                    while ((length = inputStream.read(bytes)) > 0) {
                        fileOutputStream.write(bytes, 0, length);
                        downloadCount  += length ;
                        publishProgress(downloadCount , fileSize);
                    }
                    Log.d("Download", "file download : finish");
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    inputStream.close();

                    if(file.exists())
                        return "download succeeded";
                    else
                        return "download failed";
                }
                catch(MalformedURLException e) {
                    e.printStackTrace();
                } catch(IOException e) {
                    e.printStackTrace();
                }
                return "download failed";
            }

            @Override
            protected void onPreExecute() {
                fragmentShow.setBackgroundColor(Color.rgb(245, 245, 245));
                hintText1.setVisibility(View.GONE);
                downloadNo.setVisibility(View.GONE);
                downloadYes.setVisibility(View.GONE);
                downloadProgress.setVisibility(View.VISIBLE);
                progressDemo.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                Log.d("Download task", s);

                fileFragment = new DefaultFileFragment();
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.fragmentShow, fileFragment, "default");
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.commit();
                if(s.equals( "download succeeded")){
                    Toast.makeText(getApplication(), "" + s, Toast.LENGTH_SHORT).show();
                    createFragment();
                }
            }

            @Override
            protected void onProgressUpdate(Integer... progress) {
                downloadProgress.setProgress(progress[0]);
                progressDemo.setText(progress[0]/1000 + " KB /  " + progress[1]/1000 + " KB");

            }
        }.execute(null, null, null);



    }


    private void createFragment(){

        fragmentShow.setBackgroundColor(Color.rgb(245, 245, 245));
        hintText1.setVisibility(View.GONE);
        downloadNo.setVisibility(View.GONE);
        downloadYes.setVisibility(View.GONE);
        progressDemo.setVisibility(View.GONE);
        downloadProgress.setVisibility(View.GONE);

        String type = filename.substring(filename.lastIndexOf(".")+1);
        if( type.equals("jpeg") || type.equals("gif") || type.equals("png") || type.equals("bmp") || type.equals("jpg")) {
            Log.d("Img" , type);
            imageFragment = new ImageSetFragment();
            imageFragment.setImage(dir + filename);
            (fragmentTransaction = getFragmentManager().beginTransaction()).replace(R.id.fragmentShow, imageFragment).commit();

        }else if( type.equals("mp3")){
            Log.d("Audio" , type);
            audioFrag = new AudioPlaybackFragment();
            audioFrag.setContext(getApplication());
            audioFrag.setAudioDir(dir + filename);
            (fragmentTransaction = getFragmentManager().beginTransaction()).replace(R.id.fragmentShow , audioFrag).commit();

        }else if( type.equals("3gpp") || type.equals("mpeg") || type.equals("mp4") ){
            Log.d("Video", type);
            videoFrag = new VideoPlaybackFragment();
            videoFrag.setMediaDir(dir + filename);
            videoFrag.setContext(getApplication());
            (fragmentTransaction = getFragmentManager().beginTransaction()).replace(R.id.fragmentShow, videoFrag).commit();

        }

    }

    private View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.downloadYes){
                File file = new File(dir + filename);
                if(file.exists())
                    createFragment();
                else
                    start();
            }else {
                fragmentShow.setBackgroundColor(Color.argb(255, 245, 245, 245));
                hintText1.setVisibility(View.GONE);
                downloadNo.setVisibility(View.GONE);
                downloadYes.setVisibility(View.GONE);
            }
        }
    };

}
