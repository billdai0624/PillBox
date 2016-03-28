package kro.pillbox.push;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.HashMap;

import kro.pillbox.R;

public class GCMNotificationIntentService extends IntentService {

	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	public GCMNotificationIntentService() {
		super("GcmIntentService");
	}
	String fileName = "" , userName  = "" , message = "";
    private  SharedPreferenceControl sp ;
	public static final String TAG = "GCMNotificationService";

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        sp = new SharedPreferenceControl(getApplication());
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification("Deleted messages on server: "
						+ extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {

                fileName = (String)extras.get("File Name");
				userName = (String)extras.get("User Name");
				message = (String)extras.get("Message");
                if( ( !fileName.isEmpty() ) && extras.get("Action").equals("C2C")){
                    HashMap<String , Object> hashMap = new HashMap<String, Object>();
                    int a = sp.getMessageId() + 1 ;
                    hashMap.put("Message Id" , a + "");
                    hashMap.put("File Name" ,fileName );
                    hashMap.put("User Name" ,userName );
                    hashMap.put("Message" ,message );
                    hashMap.put("isRead", false);
                    sp.storeMessageId(a);
                    if(sp.storeClientMessage(hashMap)) {
                        Log.d("store success", a + " filename  : " + fileName);
                    }
                }
				sendNotification(extras.get("SERVER_MESSAGE") + "");
				
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(String msg ) {
		Log.d(TAG, "Preparing to send notification...: " + msg);
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent(this, NavigationDrawerControl.class);
		if(!fileName.isEmpty()) {
			intent.putExtra("SERVER_MESSAGE", message);
			intent.putExtra("File Name", fileName);
			intent.putExtra("User Name", userName);
		}
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_CANCEL_CURRENT);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.icon_gcm)
				.setContentTitle("GCM XMPP Message")
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setContentText(msg);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}

}
