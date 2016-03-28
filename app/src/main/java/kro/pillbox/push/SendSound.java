package kro.pillbox.push;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import kro.pillbox.R;

/**
 * Created by Jacky on 2015/9/10.
 */
public class SendSound extends ActionBarActivity {

    TextView audioTimeCount , audioHintText ;
    ImageView audioRedPoint , audioStartButton ;
    MediaRecorder mRecorder ;
    FileDeal fileDeal ;
    SharedPreferenceControl sp ;
    Thread thread ;
    boolean isStart = false ;
    String storagePath ;
    String soundName ;
    private Handler handler ;
    int timeCount = 0 ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_sound);

        audioTimeCount = (TextView)findViewById(R.id.audioTimeCount);
        audioHintText = (TextView)findViewById(R.id.audioHintText);
        audioRedPoint = (ImageView)findViewById(R.id.audioRedPoint);
        audioStartButton = (ImageView)findViewById(R.id.audioStartButton);

        recordAudioSet();

        audioStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isStart) {
                    isStart = true;
                    audioHintText.setVisibility(View.VISIBLE);
                    audioRedPoint.setVisibility(View.VISIBLE);
                    audioStartButton.setImageResource(R.drawable.red_buuton);
                    start();
                    threadTimeCount();
                } else {
                    stop();
                    returnSound();
                    thread.interrupt();
                    thread = null ;
                    isStart = false;
                }
            }
        });

        handler = new Handler(){
            public void handleMessage(Message msg){
                timeCount++ ;
                int min , sec ;
                String tempMin , tempSec ;
                min = timeCount / 60 ;
                sec = timeCount % 60 ;
                if(min < 10)
                    tempMin = "0" + min;
                else
                    tempMin = min + "";

                if(sec < 10)
                    tempSec = "0" + sec;
                else
                    tempSec = sec + "";

                audioTimeCount.setText(tempMin + " : " + tempSec);
                super.handleMessage(msg);
            }
        };
    }

    private void start(){
        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sp.storeMediaNum(sp.SOUND_NUM, sp.readMediaNum(sp.SOUND_NUM) + 1);
    }

    private void stop(){
        mRecorder.stop();
        mRecorder.reset();
        mRecorder.release();
        mRecorder = null;
    }

    private  void returnSound(){
        Intent intent = new Intent();
        intent.putExtra("Sound Name", soundName);
        intent.putExtra("Sound Path", storagePath + soundName);
        System.out.println("return video" + intent.toString());
        setResult(2, intent);
        finish();
    }

    private void recordAudioSet(){
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        setAudioDir();
    }

    private void setAudioDir(){
        fileDeal = new FileDeal(this);
        if(!fileDeal.isDirExist("Sound")) {
            if (fileDeal.makeFolder("Sound"))
                storagePath = fileDeal.getDir("Sound");
        }
        else {
            storagePath = fileDeal.getDir("Sound");
        }
        Toast.makeText(SendSound.this, storagePath, Toast.LENGTH_LONG).show();

        mRecorder.setOutputFile(storagePath + SoundName());
        System.out.println(storagePath + SoundName());
    }

    private String SoundName() {
        sp = new SharedPreferenceControl(this);
        int temp = sp.readMediaNum(sp.SOUND_NUM);
        System.out.println(temp);
        if(temp/1000 >= 1) soundName =  "Sound" + temp + ".mp3" ;
        else if(temp/100 >= 1) soundName =  "Sound0" + temp + ".mp3" ;
        else if(temp/10 >= 1) soundName =  "Sound00" + temp + ".mp3" ;
        else soundName =  "Sound000" + temp + ".mp3";
        return soundName ;

    }

    public void threadTimeCount()
    {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (isStart) {
                        Thread.sleep(1000);
                        System.out.println("1s ago");
                        handler.sendMessage(new Message());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isStart){
            stop();
            returnSound();
            thread.interrupt();
            thread = null ;
            isStart = false;
        }
    }
}
