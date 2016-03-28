package kro.pillbox.push;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import kro.pillbox.R;

/**
 * Created by Jacky on 2015/9/19.
 */
public class SendExistFileExternal extends Fragment {

    String path ;
    String[] fileNameArray = new String[1023] , pathArray = new String[1023];
    int[] imageIdArray = new int[1023];
    File file ;
    File[] fileArray , modifyFileArry;
    SimpleAdapter sAdapter ;
    ArrayList<HashMap<String , Object>> arrayList = new ArrayList<HashMap<String, Object>>();
    int pathArrayCount ;
    ListView fileListViewExternal ;
    ImageView upOneLevelExternal;
    TextView fileDirTextExternal;

    public SendExistFileExternal(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("file list", "file external");
        return inflater.inflate(R.layout.existfile_external , container , false);
    }

    private void listFile() {
        pathArrayCount = 0;
        path = Environment.getExternalStorageDirectory().getPath() ;
        pathArray[pathArrayCount] = path ;
        fileDirTextExternal.setText("/"+pathArray[pathArrayCount].substring(pathArray[pathArrayCount].length()));
        file = new File(path);
        fileArray = file.listFiles();
        arrayHandle();

        for(int i = 0 ; i < modifyFileArry.length ; i++ ) {
            if(modifyFileArry[i] != null) {
                fileNameArray[i] = modifyFileArry[i].getName();
                checkDir(modifyFileArry[i].getPath(), i);
            }
        }

        generateList();
    }

    private void listFile(String currPath) {
        pathArray[pathArrayCount] = currPath ;
        fileDirTextExternal.setText(pathArray[pathArrayCount].substring(pathArray[0].length()));
        fileArray = null;
        modifyFileArry = new File[1023];
        file = new File(currPath);
        fileArray = file.listFiles();

        arrayHandle();
        for(int i = 0 ; i < modifyFileArry.length ; i++ ) {
            if(modifyFileArry[i] != null) {
                fileNameArray[i] = modifyFileArry[i].getName();
                checkDir(modifyFileArry[i].getPath(), i);
            }
        }
        generateList();
    }


    private void checkDir(String tempPath , int index ) {
        File check = new File(tempPath);
        if(check.isDirectory()){
            imageIdArray[index] = R.drawable.icon_bluefolder ;
        }else {
            imageIdArray[index] = R.drawable.icon_file ;
        }

    }

    private void generateList() {

        for(int i = 0 ; i <modifyFileArry.length ; i++){
            if(fileNameArray[i] != null) {
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("file Img", imageIdArray[i]);
                hashMap.put("file name", fileNameArray[i]);
                arrayList.add(hashMap);
            }
        }

        sAdapter = new SimpleAdapter(
                getActivity() ,
                arrayList ,
                R.layout.send_existfile_list,
                new String[]{"file Img" , "file name"},
                new int[]{R.id.fileTypeImg , R.id.fileNameText }
        );

        fileListViewExternal.setAdapter(sAdapter);

        fileListViewExternal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (imageIdArray[position] == R.drawable.icon_bluefolder) {
                    fileNameArray = null;
                    fileNameArray = new String[1023];
                    arrayList = new ArrayList<HashMap<String, Object>>();
                    sAdapter = null;
                    pathArrayCount ++ ;
                    listFile(modifyFileArry[position].getPath());
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("Reopen File Name" , fileNameArray[position]);
                    intent.putExtra("Reopen File Path", pathArray[pathArrayCount] + "/" + fileNameArray[position]);
                    getActivity().setResult(4, intent);
                    getActivity().finish();
                }
            }
        });

        upOneLevelExternal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pathArrayCount == 0)
                    Toast.makeText(getActivity() , "root page" , Toast.LENGTH_SHORT).show();
                else
                    back();
            }
        });
    }

    private void arrayHandle(){
        modifyFileArry = new File[1023];
        int count = 0 ;
        Log.i("file list", "array handle2");
        for(int i = 0 ; i < fileArray.length ; i++) {
            if(! ( (Character.toString(fileArray[i].getName().charAt(0))).equals(".") || fileArray[i].getName().isEmpty() ) ){
                modifyFileArry[count] = fileArray[i];
                count++;
            }
        }
    }

    public void back(){
        pathArray[pathArrayCount] = "";
        fileNameArray = null ;
        fileNameArray = new String[1023];
        arrayList = new ArrayList<HashMap<String, Object>>() ;
        sAdapter = null ;
        pathArrayCount-- ;
        listFile(pathArray[pathArrayCount]);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fileListViewExternal = (ListView)getActivity().findViewById(R.id.fileListViewExternal);
        upOneLevelExternal = (ImageView)getActivity().findViewById(R.id.upOneLevelExternal);
        fileDirTextExternal = (TextView)getActivity().findViewById(R.id.fileDirTextExternal);
        modifyFileArry = new File[1023];
        listFile();
    }
}
