package kro.pillbox.push;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.widget.TabHost;

import kro.pillbox.R;


/**
 * Created by Jacky on 2015/9/10.
 */
public class SendExistFile extends FragmentActivity {

    FragmentTabHost tabHost ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_existfile);

        tabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        tabHost.setup(this ,  getSupportFragmentManager() , R.id.container);
        tabHost.addTab(tabHost.newTabSpec("External storage").setIndicator("External storage")
                , SendExistFileExternal.class, null);
        tabHost.addTab(tabHost.newTabSpec("SDcard").setIndicator("SDcard")
                , SendExistFileSDcard.class , null);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                switch (tabId) {
                    case "External storage":
                        break;
                    case "SDcard":
                        break;
                }
            }
        });

    }
}
