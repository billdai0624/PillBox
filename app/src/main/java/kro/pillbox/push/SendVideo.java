package kro.pillbox.push;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import android.os.Handler;

import kro.pillbox.R;

/**
 * Created by Jacky on 2015/9/10.
 */
public class SendVideo extends Activity implements SurfaceHolder.Callback{

    final String MEDIA_NUM = "get media number" , VIDEO_NUM = "get video number" ;
    boolean recording = false ;
    FileDeal fileDeal ;
    private Handler handler ;
    SurfaceView surfaceView ;
    SurfaceHolder surfaceHolder ;
    TextView onRecText , recordTime ;
    ImageView redPoint , recordVideoStart;
    SharedPreferenceControl sp ;
    int timeCount = 0 , previewSizeWidth , previewSizeHeight ;
    static Camera camera = null;
    MediaRecorder mRecorder;
    String StoragePath , videoName ;
    Intent intent ;
    Thread thread ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_video);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sp = new SharedPreferenceControl(this);
        surfaceView = (SurfaceView)findViewById(R.id.videoRecordSurface);
        recordVideoStart = (ImageView)findViewById(R.id.recordVideoStart);
        onRecText = (TextView)findViewById(R.id.onRecText);
        recordTime = (TextView)findViewById(R.id.recordTime);
        redPoint = (ImageView)findViewById(R.id.redPoint);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mRecorder = new MediaRecorder();


        recordVideoStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!recording) {
                    camera.unlock();
                    MediaRecorderSet();
                    redPoint.setVisibility(View.VISIBLE);
                    recordTime.setVisibility(View.VISIBLE);
                    onRecText.setVisibility(View.VISIBLE);
                    recordVideoStart.setImageResource(R.drawable.icon_recording);
                    recording = true ;
                    camera.stopPreview();
                    start();
                }else {
                    recording = false ;
                    stop();
                    thread = null ;
                    returnVideo();
                    finish();
                }
            }
        });

        handler = new Handler(){
            public void handleMessage(Message msg) {
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

                recordTime.setText(tempMin + " : " + tempSec);
                super.handleMessage(msg);
            }
        };
    }


    private void MediaRecorderSet()
    {
        mRecorder.setCamera(camera);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setVideoFrameRate(60);
        mRecorder.setVideoSize(previewSizeWidth , previewSizeHeight);
        mRecorder.setPreviewDisplay(surfaceHolder.getSurface());

        setMediaOutputFile();
    }

    private void setMediaOutputFile() {
        fileDeal = new FileDeal(this);
        if(!fileDeal.isDirExist("Video")) {
            if (fileDeal.makeFolder("Video"))
                StoragePath = fileDeal.getDir("Video");
        }
        else {
            StoragePath = fileDeal.getDir("Video");
        }
        Toast.makeText(SendVideo.this , StoragePath , Toast.LENGTH_LONG).show();

        mRecorder.setOutputFile(StoragePath + VideoName());
        System.out.println(StoragePath + VideoName());
    }

    private String VideoName() {
        int temp = sp.readMediaNum(VIDEO_NUM);
        if(temp/1000 >= 1) videoName =  "Video" + temp + ".mp4" ;
        else if(temp/100 >= 1) videoName =  "Video0" + temp + ".mp4" ;
        else if(temp/10 >= 1) videoName =  "Video00" + temp + ".mp4" ;
        else videoName =  "Video000" + temp + ".mp4";
        return videoName ;
    }

    private void start(){
        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
           System.out.println(e.toString());
        }
        threadTimeCount();
        sp.storeMediaNum(VIDEO_NUM , sp.readMediaNum(VIDEO_NUM) + 1);
    }

    private void stop() {
        if(mRecorder != null) {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
        }
    }

    public void threadTimeCount()
    {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (recording) {
                        thread.sleep(1000);
                        handler.sendMessage(new Message());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


    private void returnVideo()
    {
        intent = new Intent();
        intent.putExtra("Video Name", videoName);
        intent.putExtra("Video Path", StoragePath + videoName);
        System.out.println("return video" + intent.toString());
        setResult(1, intent);
        finish();
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        Camera.Parameters parameters = camera.getParameters();

        List<Camera.Size> list = parameters.getSupportedPreviewSizes();

        previewSizeHeight = list.get(0).height;
        previewSizeWidth = list.get(0).width;
        for(int i = 0 ; i < list.size() ; i ++){
            if(list.get(i).height > previewSizeHeight && list.get(i).width > previewSizeWidth){
                previewSizeHeight = list.get(i).height ;
                previewSizeWidth = list.get(i).width ;
            }
        }

        parameters.setPreviewSize(previewSizeWidth , previewSizeHeight);


        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
        surfaceHolder = holder ;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null ;
        surfaceView = null ;
        mRecorder = null ;
        surfaceHolder = null ;
        recording = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(recording) {
            recording = false ;
            returnVideo();
            thread = null ;
        }
    }
}
