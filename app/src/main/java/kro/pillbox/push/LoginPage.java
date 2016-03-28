package kro.pillbox.push;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import kro.pillbox.R;


/**
 * Created by Jacky on 2015/11/20.
 */
public class LoginPage extends Activity{

    EditText phoneNum , userName ;
    Button registerButton ;
    SharedPreferenceControl sp ;
    GCMHandle gcmHandle ;
    GetContacts getContacts ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        sp = new SharedPreferenceControl(this);

        if(sp.getUserInform("User name") != null){
            Intent intent = new Intent(LoginPage.this, NavigationDrawerControl.class);
            startActivity(intent);
            finish();
        }else{
            gcmHandle = new GCMHandle(getApplicationContext());
            gcmHandle.registerGCM();
            getContacts = new GetContacts(getApplication());
        }

        phoneNum = (EditText)findViewById(R.id.phoneNumberText);
        userName = (EditText)findViewById(R.id.userNameText);
        registerButton = (Button)findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(phoneNum.getText().toString()) || TextUtils.isEmpty(userName.getText().toString())) {
                    Toast.makeText(getApplication(), "Phone number and user name \n can not be null", Toast.LENGTH_LONG).show();
                } else {
                    synchronizeContact();
                }
            }
        });

    }

    private void synchronizeContact(){
        new AsyncTask<Void , Void , String>(){
            @Override
            protected void onPostExecute(String msg) {
                sp.storeUserInform("User name", userName.getText().toString());
                sp.storeUserInform("Phone number", phoneNum.getText().toString());
                gcmHandle.sendMessage("Upload User Msg", "", phoneNum.getText().toString(), userName.getText().toString(), "");
                Intent intent = new Intent(LoginPage.this, NavigationDrawerControl.class);
                startActivity(intent);
                finish();
            }

            @Override
            protected String doInBackground(Void... params) {
                getContacts.getContactsInformation();
                return "Done";
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
        }.execute(null, null, null);
    }
}
