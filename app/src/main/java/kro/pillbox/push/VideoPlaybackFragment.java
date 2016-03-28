package kro.pillbox.push;

import android.app.Fragment;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;

import kro.pillbox.R;

/**
 * Created by Jacky on 2015/10/8.
 */
public class VideoPlaybackFragment extends Fragment {

    Context context ;
    VideoView videoView;
    private Thread thread;
    String dir ;
    SeekBar videoViewBar ;
    TextView videoViewTime ;
    int  videoLength ;
    boolean isPlaying  = false ;
    Button buttonVideoStart ,  buttonVideoPause ;

    public VideoPlaybackFragment(){

    }

    public void setContext(Context context){
        this.context = context ;
    }

    public void setMediaDir(String dir){
        this.dir = dir;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.video_playback_fragment, container , false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("test frag", "initial2");
        videoView = (VideoView)getActivity().findViewById(R.id.videoView);
        videoViewTime = (TextView)getActivity().findViewById(R.id.videoViewTime);
        videoViewBar = (SeekBar)getActivity().findViewById(R.id.videoViewBar);

        videoView.setVideoPath(dir);
        videoView.setFocusable(true);
        videoViewBar.setOnSeekBarChangeListener(onchange);
        videoView.setOnClickListener(onclick);
        videoView.setOnPreparedListener(onPrepared);
        videoView.setOnCompletionListener(onComplete);

        play();
    }

    private void play(){
        videoView.start();
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                isPlaying = true;
                try {
                    while (isPlaying){
                        int current = videoView.getCurrentPosition();
                        videoViewBar.setProgress(current);
                        handler.sendMessage(new Message());
                        thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        Log.d("thread", "gogo");
    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int current = videoView.getCurrentPosition() / 1000 ;

            if(current > videoViewBar.getMax()/1000)
                current = videoViewBar.getMax()/1000;
            videoViewTime.setText(timeTypeTransfer(current));
            super.handleMessage(msg);
        }
    };

    private void pause(){

        videoView.pause();
        isPlaying = false;
        if(thread.isAlive())
            thread.interrupt();
        thread = null ;
        Toast.makeText(context , "pause" , Toast.LENGTH_SHORT).show();
    }

    private String timeTypeTransfer(int msc){
        int min , sec ;
        String tempMin , tempSec ;
        min = msc / 60 ;
        sec = msc % 60 ;
        if(min < 10)
            tempMin = "0" + min;
        else
            tempMin = min + "";

        if(sec < 10)
            tempSec = "0" + sec;
        else
            tempSec = sec + "";

        return tempMin + " : " + tempSec ;

    }

    private SeekBar.OnSeekBarChangeListener onchange = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress =  videoViewBar.getProgress();
            isPlaying = true ;
            if(videoView != null ) {

                if(!videoView.isPlaying()) {
                    play();
                }
                videoView.seekTo(progress);
            }
        }
    };

    private View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i("onclick" , "check");
            if(!isPlaying) {
                int progress = videoView.getCurrentPosition();
                isPlaying = true ;
                videoView.seekTo(progress);
            } else {
                pause();
            }
        }
    };

    private MediaPlayer.OnPreparedListener onPrepared = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            videoLength = videoView.getDuration();
            videoViewBar.setMax(videoLength);
            videoViewBar.setProgress(0);
            videoView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT ;
            videoView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT ;
            Log.i("play", "duration : " + videoLength + "  clickable :" + videoView.isClickable());
        }
    };

    private MediaPlayer.OnCompletionListener onComplete = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if(thread != null)
                thread.interrupt();
            thread = null;
            isPlaying = false;
            Log.d("onComplete" , "running");
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if(isPlaying){
            pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isPlaying = false;
        thread = null;
    }
}
