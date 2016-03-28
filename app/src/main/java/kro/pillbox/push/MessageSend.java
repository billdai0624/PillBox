package kro.pillbox.push;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import kro.pillbox.R;

/**
 * Created by Jacky on 2015/9/10.
 */
public class MessageSend extends ActionBarActivity {

    SharedPreferenceControl sp;
    final String ROOT_DIR = "get root dir";
    LinearLayout  fourItemMenuLocate;
    RelativeLayout grayControl;
    Button sendToUser ;
    boolean isPress = false;
    EditText textMessage;
    ImageView chooseMedia;
    TextView  fileTypeHint;
    FileDeal fileDeal ;
    FourItemMenuFragment fragment ;
    FragmentTransaction fragmentTransaction ;
    String targetFileDir = "" , targetFileName = ""  ;
    String[] phoneNum  , userName ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_send);
        sp = new SharedPreferenceControl(this);
        Intent intent = getIntent();
        phoneNum = intent.getStringArrayExtra("Phone Number");
        userName = intent.getStringArrayExtra("User Name");

        textMessage = (EditText)findViewById(R.id.textMessage);
        sendToUser = (Button)findViewById(R.id.sendToUser);
        fourItemMenuLocate = (LinearLayout)findViewById(R.id.fourItemMenuLocate);
        grayControl = (RelativeLayout)findViewById(R.id.grayControl);
        fileTypeHint = (TextView)findViewById(R.id.fileTypeHint);
        chooseMedia = (ImageView)findViewById(R.id.chooseMedia);

        sendToUser.setOnClickListener(handleClickListener);
        chooseMedia.setOnClickListener(handleClickListener);
        grayControl.setOnClickListener(handleClickListener);

        fileDeal = new FileDeal(this);
        if(!fileDeal.isDirExist("Root")){
            if(fileDeal.makeFolder("Root"))
                Toast.makeText(MessageSend.this , "mkdir success" , Toast.LENGTH_LONG).show();
        }
        else
            Toast.makeText(MessageSend.this , "file exist" , Toast.LENGTH_LONG).show();

    }

    private View.OnClickListener handleClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent ;
            switch (v.getId())
            {
                case R.id.sendToUser:
                    intent = new Intent(MessageSend.this , C2CMsgSendPage.class);
                    String temp ;
                    if(textMessage.getText() == null)
                        temp = "";
                    else
                        temp = textMessage.getText().toString();
                        intent.putExtra("Text message",temp);
                        intent.putExtra("Target file path" , targetFileDir);
                        intent.putExtra("Target file name" , targetFileName);
                        intent.putExtra("Phone Number", phoneNum);
                        intent.putExtra("User Name" , sp.getUserInform("User name"));
                        startActivity(intent);
                        finish();
                        break;
                case R.id.chooseMedia:
                    if(!isPress){
                        fragmentSet("in");
                        isPress = true ;
                    }else {
                        fragmentSet("out");
                        isPress = false ;
                    }
                    break;
                case R.id.grayControl:
                    fragmentSet("out");
                    isPress = false ;

            }
        }
    };


    private void fragmentSet(String act){
        if(act.equals("in")){
            fragment = new FourItemMenuFragment();
            fragment.setContext(this);
            grayControl.setVisibility(View.VISIBLE);
            fourItemMenuLocate.setVisibility(View.VISIBLE);
            fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.fourItemMenuLocate, fragment, "test");
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
        }else {
            fragment = null ;
            fragmentTransaction.remove(fragment);
            fragmentTransaction = null ;
            grayControl.setVisibility(View.GONE);
            fourItemMenuLocate.setVisibility(View.GONE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode < 4 && resultCode > 0) {
            fragment = null;
            fragmentTransaction.remove(fragment);
            fragmentTransaction = null;
            grayControl.setVisibility(View.GONE);
            fourItemMenuLocate.setVisibility(View.GONE);
            isPress = false;
        }
        System.out.println("return success");
        switch (resultCode)
        {
            case 0 : Toast.makeText(this , "return nothing" , Toast.LENGTH_SHORT).show();break;
            case 1 : fileTypeHint.setText("Send file " + data.getStringExtra("Video Name") + " . ");targetFileDir = data.getStringExtra("Video Path") ; targetFileName = data.getStringExtra("Video Name");break;
            case 2 : fileTypeHint.setText("Send file " + data.getStringExtra("Sound Name") + " . ");targetFileDir = data.getStringExtra("Sound Path") ; targetFileName = data.getStringExtra("Sound Name");break;
            case 3 : fileTypeHint.setText("Send file " + data.getStringExtra("Image Name") + " . ");targetFileDir = data.getStringExtra("Image Path") ; targetFileName = data.getStringExtra("Image Name");break;
            case 4 : fileTypeHint.setText("Send file " + data.getStringExtra("Reopen File Name") + " . ");targetFileDir = data.getStringExtra("Reopen File Path") ; targetFileName = data.getStringExtra("Reopen File Name");break;
        }
    }
}
