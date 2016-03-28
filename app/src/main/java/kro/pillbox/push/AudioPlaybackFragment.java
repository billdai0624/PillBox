package kro.pillbox.push;

import android.app.Fragment;import kro.pillbox.R;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

/**
 * Created by Jacky on 2015/10/15.
 */
public class AudioPlaybackFragment extends Fragment{

    private String dir ;
    TextView audioTimeCount ;
    ImageView audioStateControl;
    SeekBar audioSeekBar ;
    Context context;
    MediaPlayer mp ;
    boolean isPlaying = false;
    Thread thread ;

    public void setContext(Context context){
        this.context = context ;
    }

    public void setAudioDir(String dir){
        this.dir = dir;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.audio_playback_fragment , container , false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        audioTimeCount = (TextView)getActivity().findViewById(R.id.audioTimeCount);
        audioStateControl = (ImageView)getActivity().findViewById(R.id.audioStateControl);
        audioSeekBar = (SeekBar)getActivity().findViewById(R.id.audioSeekBar);
        audioSeekBar.setOnSeekBarChangeListener(onChange);
        audioStateControl.setOnClickListener(onclick);

        initial();
    }

    private void initial(){
        mp = new MediaPlayer();
        mp.setOnPreparedListener(onPrepare);
        mp.setOnCompletionListener(onComplete);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            Log.i("media player" , "set data source");
            mp.setDataSource(dir);
            mp.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void start(int msc){
        isPlaying = true ;
        audioStateControl.setImageResource(R.drawable.icon_pause);
        mp.seekTo(msc);
        mp.start();

        thread = new Thread(){
            @Override
            public void run() {
                try {
                    while (isPlaying){
                        int cur = mp.getCurrentPosition();
                        audioSeekBar.setProgress(cur);
                        handler.sendMessage(new Message());
                        thread.sleep(100);
                    }
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    private void pause(){
        audioStateControl.setImageResource(R.drawable.icon_play2);

        if(thread.isAlive())
            thread.interrupt();
        thread = null ;

        mp.pause();

        isPlaying = false;
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int cur = 0;
            if(mp != null) {
                cur = mp.getCurrentPosition() / 1000;

                if (cur > audioSeekBar.getMax() / 1000)
                    cur = audioSeekBar.getMax() / 1000;
                audioTimeCount.setText(timeTypeTransfer(cur));

                super.handleMessage(msg);
            }
        }
    };

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

    private SeekBar.OnSeekBarChangeListener onChange = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = audioSeekBar.getProgress();
            if(!mp.isPlaying()){
                start(progress);
                isPlaying = true ;
            }else {
                mp.seekTo(progress);
            }
        }
    };

    private View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!isPlaying){
                Log.i("seek bar" , audioSeekBar.getProgress() + "");
                start(audioSeekBar.getProgress() );
            }else {
                pause();
            }
        }
    };

    private MediaPlayer.OnPreparedListener onPrepare = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            int max = mp.getDuration();
            audioSeekBar.setMax(max);
            Log.i("Audio Duration" , max + " ");
        }
    };

    private MediaPlayer.OnCompletionListener onComplete = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            audioStateControl.setImageResource(R.drawable.icon_repeat);
            isPlaying = false;
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        pause();
    }


}
