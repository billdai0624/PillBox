package kro.pillbox.push;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import kro.pillbox.R;


/**
 * Created by Jacky on 2015/10/10.
 */
public class ImageSetFragment extends Fragment {

    Context context ;
    ImageView imgSet ;
    Uri uri ;
    String filePath;

    public void setImage(String fileDirectory){
        filePath = fileDirectory;
        uri = Uri.parse(fileDirectory);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.image_set_fragment, container ,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imgSet = (ImageView)getActivity().findViewById(R.id.imgSet);
        imgScale();
    }

    private void imgScale(){
        Log.i("image show fragment" , "1111");
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        int imgWidth = bitmap.getWidth();
        int imgHeight = bitmap.getHeight();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        imgSet.setImageURI(uri);
        imgSet.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                /*(screenWidth * 4 / 5 / imgWidth) * imgHeight;*/
        imgSet.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT ;
    }

}
