package kro.pillbox.push;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import kro.pillbox.R;

/**
 * Created by Jacky on 2015/11/28.
 */
public class MediaMessageList extends AppCompatActivity {

    ListView mediaMessageList ;
    ArrayList<HashMap<String , Object>> arrayList ;
    SimpleAdapter simpleAdapter ;
    SharedPreferenceControl sp ;
    String NameArray[] , filename[] , MsgArray[];
    int indexJ[] ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_message_list);
        final Intent intent = getIntent();
        String title = intent.getStringExtra("Title hint");
        setTitle(title);
        sp = new SharedPreferenceControl(this);
        mediaMessageList = (ListView)findViewById(R.id.mediaMessageList);

        NameArray = new String[sp.getMessageId()];
        MsgArray = new String[sp.getMessageId()];
        filename = new String[sp.getMessageId()];
        indexJ = new int[sp.getMessageId()];

        if(title.equals("New message")){
            initialUnReadList();
        }else {
            initialReadList();
        }

        mediaMessageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent newIntent = new Intent(MediaMessageList.this , MessageShow.class);
                newIntent.putExtra("User Name" , NameArray[position]);
                newIntent.putExtra("Message" , MsgArray[position]);
                newIntent.putExtra("File Name" , filename[position]);
                newIntent.putExtra("Index" , indexJ[position]);
                startActivity(newIntent);
                finish();
            }
        });

    }

    private void initialReadList(){
        int j = 0;
        arrayList = new ArrayList<HashMap<String , Object>>();
        for(int i = 1 ; i <= sp.getMessageId() ; i++){
            HashMap<String , Object> hashMap = sp.getClientMessage(i);
            if((boolean)hashMap.get("isRead")){
                NameArray[j] = (String)hashMap.get("User Name");
                MsgArray[j] = (String)hashMap.get("Message");
                filename[j] = (String)hashMap.get("File Name");
                indexJ[j] = i ;
                j++ ;
            }
        }
        initialAdapter();
    }

    private void initialUnReadList(){
        int j = 0;
        arrayList = new ArrayList<HashMap<String , Object>>();
        for(int i = 1 ; i <= sp.getMessageId() ; i++){
            HashMap<String , Object> hashMap = sp.getClientMessage(i);
            if(!( (boolean)hashMap.get("isRead") ) ){
                NameArray[j] = (String)hashMap.get("User Name");
                MsgArray[j] = (String)hashMap.get("Message");
                filename[j] = (String)hashMap.get("File Name");
                indexJ[j] = i ;
                j++ ;
            }
        }
        initialAdapter();
    }

    public void initialAdapter(){
        for(int i = 0 ; i < NameArray.length ; i++){
            if(NameArray[i] != null){
                HashMap<String , Object> hashMap = new HashMap<String, Object>();
                hashMap.put("Message" , "Message : " +MsgArray[i]);
                hashMap.put("Name", "User name : " +NameArray[i]);
                arrayList.add(hashMap);
            }
        }
        simpleAdapter = new SimpleAdapter(
                this,
                arrayList,
                android.R.layout.simple_list_item_2,
                new String[]{"Message" , "Name"},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        mediaMessageList.setAdapter(simpleAdapter);
    }
}
