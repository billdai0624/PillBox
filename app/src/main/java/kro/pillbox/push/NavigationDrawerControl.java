package kro.pillbox.push;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import kro.pillbox.R;


public class NavigationDrawerControl extends AppCompatActivity {

    SharedPreferenceControl sp ;
    Fragment relationUserFragment , mentionTimeSetFragment, BT_connection , progressBarFragment , messageControlFragment;
    FragmentTransaction fragmentTransaction ;

    private DrawerLayout drawerLayout;
    private ListView drawerList ;
    private RelativeLayout drawerContent ;
    private TextView userName , phoneNumber;
    private ImageButton userIcon ;
    private FrameLayout frameLayout ;
    public boolean[] checked;

    private ActionBarDrawerToggle actionBarDrawerToggle ;
    String[] features ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);

        sp = new SharedPreferenceControl(this);

        userName = (TextView)findViewById(R.id.userName);
        phoneNumber = (TextView)findViewById(R.id.phoneNumber);
        userIcon = (ImageButton)findViewById(R.id.userIcon);
        drawerContent = (RelativeLayout)findViewById(R.id.drawerContent);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        frameLayout = (FrameLayout)findViewById(R.id.frameLayout);
        drawerList = (ListView)findViewById(R.id.drawerList);
        userName.setOnClickListener(onClick);
        userIcon.setOnClickListener(onClick);

        init();
        drawerList.setItemChecked(0, true);

        messageControlFragment = new MessageControlFragment();
        (fragmentTransaction = getFragmentManager().beginTransaction()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.frameLayout, messageControlFragment).commit();
    }

    public void init(){
        phoneNumber.setText(sp.getUserInform("Phone number"));
        userName.setText(sp.getUserInform("User name"));
        initActionBar();
        initDrawer();
        initDrawList();
    }

    private void initActionBar(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initDrawer(){
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow , GravityCompat.START);

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.drawable_open,
                R.string.drawable_close
        ){};
        actionBarDrawerToggle.syncState();
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        drawerList.setOnItemClickListener(onItemClick);
    }

    private void initDrawList(){
        features = getResources().getStringArray(R.array.function_list);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this , R.layout.list_features , features);
        drawerList.setAdapter(arrayAdapter);

    }

    private ListView.OnItemClickListener onItemClick = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    };

    private void selectItem(int position){
        switch (position){
            case 0 :
                messageControlFragment = new MessageControlFragment();
                (fragmentTransaction = getFragmentManager().beginTransaction()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.frameLayout, messageControlFragment).commit();
                break;
            case 1 :
                break;
            case 2 :
                mentionTimeSetFragment = new MentionTimeSetFragment();
                (fragmentTransaction = getFragmentManager().beginTransaction()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.frameLayout, mentionTimeSetFragment).commit();
                break;
            case 3 :
                break;
            case 4 :
                relationUserFragment = new RelationUserFragment();
                (fragmentTransaction = getFragmentManager().beginTransaction()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.frameLayout, relationUserFragment).commit();
                break;
        }
        drawerList.setItemChecked(position , true);
        setTitle(features[position]);
        drawerLayout.closeDrawer(drawerContent);
    }

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.userIcon :
                    Toast.makeText(getApplication() , "change user icon" , Toast.LENGTH_SHORT).show();
                    break;
                case R.id.userName :
                    Toast.makeText(getApplication() , "change user name" , Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    } ;

    private void synchronizeContact(){
        new AsyncTask<Void , Void , String>(){
            @Override
            protected void onPostExecute(String msg) {
                selectItem(4);
                Toast.makeText(getApplication() , "synchronize : " + msg , Toast.LENGTH_SHORT).show();
            }

            @Override
            protected String doInBackground(Void... params) {
                progressBarFragment = new ProgressBarFragment();
                (fragmentTransaction = getFragmentManager().beginTransaction()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.frameLayout, progressBarFragment).commit();
                GetContacts getContacts = new GetContacts(getApplication());
                getContacts.getContactsInformation();
                return "Done";
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
        }.execute(null, null , null );
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }else if(item.getItemId() == R.id.action_synchronize){
            synchronizeContact();
        }

        return super.onOptionsItemSelected(item);
    }
}
