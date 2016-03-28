package kro.pillbox;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Bluetooth extends AppCompatActivity {
    private static BluetoothAdapter mBluetoothAdapter = null;
    Handler handler;
    TextView stat;
    Button search, stop;
    boolean bound = false;
    boolean serviceRunning = false;
    private BluetoothService.MyBinder myBinder;
    private ServiceConnection serviceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth);
        stat = (TextView) findViewById(R.id.stat);
        search = (Button) findViewById(R.id.search);
        stop = (Button) findViewById(R.id.stop);
        serviceRunning = isMyServiceRunning(BluetoothService.class);
        if (!serviceRunning) {
            stat.setText("System offline.");
        }
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                myBinder = (BluetoothService.MyBinder) service;
                myBinder.getService().setOnUpdateUIListener(new BluetoothService.updateUIListener() {
                    @Override
                    public void onUpdate(String s) {
                        final String temp = s;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                stat.setText(temp);
                            }
                        });

                    }
                });
                if (!serviceRunning) {
                    myBinder.searchBT();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Intent bindIntent = new Intent(getApplicationContext(), BluetoothService.class);
        if (serviceRunning) {
            bound = getApplicationContext().bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
            stat.setText("System online.");
        }
        getApplicationContext().startService(bindIntent);
        handler = new Handler();
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkBT();
                //searchBT();
                Intent bindIntent = new Intent(getApplicationContext(), BluetoothService.class);
                getApplicationContext().startService(bindIntent);
                if (!bound) {
                    bound = getApplicationContext().bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
                }
            }

        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stopIntent = new Intent(getApplicationContext(), BluetoothService.class);
                if (bound) {
                    getApplicationContext().unbindService(serviceConnection);
                    bound = serviceRunning = false;
                    stat.setText("System offline.");
                }

                getApplicationContext().stopService(stopIntent);
            }
        });
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bound) {
            getApplicationContext().unbindService(serviceConnection);
        }
    }
}