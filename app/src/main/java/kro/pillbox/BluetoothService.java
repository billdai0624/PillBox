package kro.pillbox;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class BluetoothService extends Service {
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static BluetoothAdapter mBluetoothAdapter = null;
    public static BluetoothSocket mBluetoothSocket = null;
    public static InputStream mInputStream = null;
    public updateUIListener updateUIListener;
    public Thread searchBT;
    public Runnable runnable;
    public boolean running = false;
    public MyBinder myBinder = new MyBinder();
    private BroadcastReceiver mReceiver;
    private Intent notificationIntent;
    private NotificationManager notifyManager;
    private NotificationCompat.Builder notification;

    public void setOnUpdateUIListener(updateUIListener updateUIListener) {
        this.updateUIListener = updateUIListener;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        running = false;
        runnable = new Runnable() {
            @Override
            public void run() {
                searchBT();
                running = true;
            }
        };
        searchBT = new Thread(runnable);
        notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notification = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("Smart pill box").setContentText("Medicine detected!")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.pill)
                .setAutoCancel(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //searchBT();
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (running) {
                mBluetoothSocket.close();
                mBluetoothSocket = null;
                running = false;
                unregisterReceiver(mReceiver);
            }
        } catch (IOException e) {
            Log.e("ERROR", e.toString());
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public void searchBT() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.startDiscovery();
        updateUIListener.onUpdate("Scanning device...");

        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                    if (device.getName().equals("HC-06")) {
                        try {
                            mBluetoothAdapter.cancelDiscovery();
                            updateUIListener.onUpdate("Device is discovered.Connecting to device...");
                            Thread t = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (mBluetoothAdapter.isDiscovering()) {
                                            mBluetoothAdapter.cancelDiscovery();
                                        }
                                        try {
                                            /*Version > Android2.3*/
                                            mBluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                                            /*Version<= Android2.3*/

                                            /*Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                                            mBluetoothSocket = (BluetoothSocket) m.invoke(device, Integer.valueOf(1));*/

                                            mBluetoothSocket.connect();
                                            updateUIListener.onUpdate("Device is connected.\nSystem online.");
                                            Thread cted = new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        mInputStream = mBluetoothSocket.getInputStream();
                                                    } catch (IOException e) {
                                                        Log.e("ERROR", e.toString());
                                                    }
                                                    int bytes;
                                                    while (true) {
                                                        try {
                                                            if (mInputStream.available() > 0) {
                                                                updateUIListener.onUpdate("Medicine detected.");
                                                                bytes = mInputStream.read();
                                                                notifyManager.notify(1, notification.build());
                                                            }
                                                        } catch (IOException e) {
                                                            Log.e("ERROR", e.toString());
                                                            break;
                                                        }
                                                    }
                                                }
                                            });
                                            cted.start();
                                        } catch (IOException connectException) {
                                            Log.e("ERROR", connectException.toString());
                                        }
                                    } catch (Exception e) {
                                        Log.e("ERROR", e.toString());
                                    }
                                }
                            });
                            t.start();
                        } catch (Exception e) {
                            Log.e("ERROR, ", e.toString());
                        }
                    }

                }
            }
        };
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);


    }

    public interface updateUIListener {
        void onUpdate(String s);
    }

    class MyBinder extends Binder {
        public void searchBT() {
            if (!running) {
                searchBT.start();
                running = true;
            }
        }

        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }
}
