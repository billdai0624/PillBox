package kro.pillbox.push;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by Jacky on 2015/9/11.
 */
public class FileDeal {

    String VideoStoragePath , SoundStoragePath , ImageStoragePath , Client2ClientRootPath , DownloadPath , storagePath ;
    final String ROOT_DIR = "get root dir", VIDEO_DIR = "get video file dir" , SOUND_DIR = "get sound file dir" , IMG_DIR = "get img file dir" , DOWNLOAD_DIR = "get download dir";
    File file;
    SharedPreferenceControl sp ;

    FileDeal(Context context){
        sp = new SharedPreferenceControl(context);
        storagePath = Environment.getExternalStorageDirectory().getPath();
        Client2ClientRootPath = storagePath + "/Client2Client" ;
        DownloadPath =  Client2ClientRootPath + "/Download" ;
        ImageStoragePath = Client2ClientRootPath + "/Image" ;
        SoundStoragePath = Client2ClientRootPath + "/Sound" ;
        VideoStoragePath = Client2ClientRootPath + "/Video" ;
    }

    public boolean isDirExist(String folderName){
        String tempPath = "", tempKey = "";
        switch (folderName)
        {
            case "Root" : tempPath = Client2ClientRootPath ; tempKey = ROOT_DIR ; break;
            case "Video" : tempPath = VideoStoragePath ; tempKey = VIDEO_DIR ; break;
            case "Sound" : tempPath = SoundStoragePath ; tempKey = SOUND_DIR ; break;
            case "Image" : tempPath = ImageStoragePath ; tempKey = IMG_DIR ;;break;
            case "Download" : tempPath = DownloadPath ; tempKey = DOWNLOAD_DIR ;break;
        }
        file = new File(tempPath);

        if(file.exists()){
            sp.storeDir(tempKey , tempPath );
            return true;
        }
        else
            return false;
    }

    public boolean makeFolder(String folderName){
        String tempPath = "", tempKey = "";
        switch (folderName)
        {
            case "Root" : tempPath = Client2ClientRootPath ; tempKey = ROOT_DIR ; break;
            case "Video" : tempPath = VideoStoragePath ; tempKey = VIDEO_DIR ; break;
            case "Sound" : tempPath = SoundStoragePath ; tempKey = SOUND_DIR ; break;
            case "Image" : tempPath = ImageStoragePath ; tempKey = IMG_DIR ;break;
            case "Download" : tempPath = DownloadPath ; tempKey = DOWNLOAD_DIR ;break;
        }
        file = new File(tempPath);

        if(file.mkdir()) {
            sp.storeDir(tempKey , tempPath);
            return true;
        }
        else
            return false;
    }

    public String getDir(String folderName){
        String  tempKey = "";
        switch (folderName)
        {
            case "Root" :  tempKey = ROOT_DIR ; break;
            case "Video" : tempKey = VIDEO_DIR ; break;
            case "Sound" : tempKey = SOUND_DIR ; break;
            case "Image" : tempKey = IMG_DIR ; break;
            case "Download" : tempKey = DOWNLOAD_DIR ;
        }
        return sp.getDir(tempKey) + "/";
    }



}
