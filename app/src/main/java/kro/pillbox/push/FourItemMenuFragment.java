package kro.pillbox.push;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import kro.pillbox.R;

/**
 * Created by Jacky on 2015/9/17.
 */
public class FourItemMenuFragment extends Fragment {

    LinearLayout sendExistFile , sendVideo , sendSound , sendImg , fourItemMenu ;
    Context context ;

    public FourItemMenuFragment(){

    }

    public void setContext(Context context){
        this.context = context ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.four_item_fragment , container , false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fourItemMenu = (LinearLayout)getActivity().findViewById(R.id.fourItemMenu);
        sendExistFile = (LinearLayout)getActivity().findViewById(R.id.sendExistFile);
        sendVideo = (LinearLayout)getActivity().findViewById(R.id.sendVideo);
        sendSound = (LinearLayout)getActivity().findViewById(R.id.sendSound);
        sendImg = (LinearLayout)getActivity().findViewById(R.id.sendImg);

        sendExistFile.setOnClickListener(handleClickListener);
        sendImg.setOnClickListener(handleClickListener);
        sendSound.setOnClickListener(handleClickListener);
        sendVideo.setOnClickListener(handleClickListener);

    }

    private View.OnClickListener handleClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent ;
            switch (v.getId()) {
                case R.id.sendExistFile:
                    intent = new Intent(context, SendExistFile.class);
                    getActivity().startActivityForResult(intent, 0);
                    break;
                case R.id.sendVideo:
                    intent = new Intent(context, SendVideo.class);
                    System.out.println(intent.toString());
                    getActivity().startActivityForResult(intent, 0);
                    break;
                case R.id.sendSound:
                    intent = new Intent(context, SendSound.class);
                    getActivity().startActivityForResult(intent, 0);
                    break;
                case R.id.sendImg:
                    intent = new Intent(context, SendImg.class);
                    getActivity().startActivityForResult(intent, 0);
            }
        }
    };

}
