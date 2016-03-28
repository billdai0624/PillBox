package kro.pillbox.push;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kro.pillbox.R;

/**
 * Created by Jacky on 2015/9/19.
 */
public class SendExistFileSDcard extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.existfile_sdcard, container, false);
    }
}
