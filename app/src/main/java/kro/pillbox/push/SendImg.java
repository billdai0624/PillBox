package kro.pillbox.push;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;


import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import kro.pillbox.R;

/**
 * Created by Jacky on 2015/9/10.
 */
public class SendImg extends Activity implements SurfaceHolder.Callback{

    SurfaceView imgPreview ;
    SurfaceHolder sHolder ;
    Button imgCapture ;
    boolean previewing = false;
    Camera camera;
    FileDeal fileDeal ;
    SharedPreferenceControl sp ;
    String imageName ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_img);
        imgPreview = (SurfaceView)findViewById(R.id.imgPreView);
        imgCapture = (Button)findViewById(R.id.imgCapture);
        imgCapture.setOnClickListener(onclick);
        sp = new SharedPreferenceControl(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sHolder = imgPreview.getHolder();
        sHolder.addCallback(this);
        sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        generateStoreDir();
    }

    private void generateStoreDir() {

        fileDeal = new FileDeal(this);
        if(!fileDeal.isDirExist("Root")){
            if(fileDeal.makeFolder("Root")) {
                if(fileDeal.makeFolder("Image"))
                    Toast.makeText(this, "mkdir success", Toast.LENGTH_LONG).show();
            }
        }
        else {
            if(!fileDeal.isDirExist("Image")){
                if(fileDeal.makeFolder("Image")){
                    Toast.makeText(this, "mkdir success", Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(this, "file exist", Toast.LENGTH_LONG).show();
            }
        }
    }

    private  void returnImage(){
        Intent intent = new Intent();
        intent.putExtra("Image Name", imageName);
        intent.putExtra("Image Path", fileDeal.getDir("Image") + imageName);
        setResult(3, intent);
        finish();
    }

    private View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            camera.takePicture(shutterCallback , null , pictureCallback);
        }
    };

    private Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {

        }
    };

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data , 0 , data.length);
            FileOutputStream fileOutputStream = null ;
            String filename = fileDeal.getDir("Image") + imageName();
            Log.i("locate img" , filename);
            try {
                fileOutputStream = new FileOutputStream(filename);
                bitmap.compress(Bitmap.CompressFormat.JPEG , 80 , fileOutputStream);
            }catch (Exception e){
                e.printStackTrace();
            }
            returnImage();
        }
    };

    private String imageName() {
        int temp = sp.readMediaNum(sp.IMG_NUM);
        if(temp/1000 >= 1) imageName =  "Image" + temp + ".jpg" ;
        else if(temp/100 >= 1) imageName =  "Image0" + temp + ".jpg" ;
        else if(temp/10 >= 1) imageName =  "Image00" + temp + ".jpg" ;
        else imageName =  "Image000" + temp + ".jpg";
        sp.storeMediaNum(sp.IMG_NUM , temp+1);
        return imageName;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (previewing){
            camera.stopPreview();
            previewing = false;
        }

        Camera.Parameters parameters = camera.getParameters();
        parameters.setFlashMode("auto");
        parameters.setPictureFormat(PixelFormat.JPEG);

        Camera.Size size ;
        List<Camera.Size> list = camera.getParameters().getSupportedPreviewSizes();
        size = list.get(0);

        for(int i = 1 ; i < list.size() ; i++){
            if(list.get(i).height * list.get(i).width > size.height * size.width){
                size = list.get(i);
            }
        }

        parameters.setPreviewSize(size.width , size.height);
        parameters.setPictureSize(size.width , size.height);

        try {
            camera.setPreviewDisplay(holder);
            camera.setParameters(parameters);
            camera.startPreview();
            previewing = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null ;
        previewing = false;
    }
}
