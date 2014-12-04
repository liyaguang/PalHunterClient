package edu.usc.palhunter;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {
  private static final String TAG = "IntentService";

  public static final int NOTIFICATION_ID = 1;
  private NotificationManager mNotificationManager;
  NotificationCompat.Builder builder;

  public GcmIntentService() {
    super("GcmIntentService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    Bundle extras = intent.getExtras();
    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
    // The getMessageType() intent parameter must be the intent you received
    // in your BroadcastReceiver.
    String messageType = gcm.getMessageType(intent);

    if (!extras.isEmpty()) { // has effect of unparcelling Bundle
      /*
       * Filter messages based on message type. Since it is likely that GCM will
       * be extended in the future with new message types, just ignore any
       * message types you're not interested in, or that you don't recognize.
       */
      if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
        sendNotification("Send error: " + extras.toString());
      } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
        sendNotification("Deleted messages on server: " + extras.toString());
        // If it's a regular GCM message, do some work.
      } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
        // This loop represents the service doing some work.
        for (int i = 0; i < 2; i++) {
          Log.i(TAG,
              "Working... " + (i + 1) + "/5 @ " + SystemClock.elapsedRealtime());
          try {
            Thread.sleep(100);
          } catch (InterruptedException e) {
          }
        }
        Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
        // Post notification of received message.
        String content = extras.getString("content");
        sendNotification(content);
        Log.i(TAG, "Received: " + extras.toString());
      }
    }
    // Release the wake lock provided by the WakefulBroadcastReceiver.
    GcmBroadcastReceiver.completeWakefulIntent(intent);
  }

  /**
   * Put the message into a notification and post it. This is just one simple
   * example of what you might choose to do with a GCM message.
   */
  private void sendNotification(String msg) {
    mNotificationManager = (NotificationManager) this
        .getSystemService(Context.NOTIFICATION_SERVICE);

    Intent resultIntent = new Intent(this, MapActivity.class);
    // Intent resultIntent = new Intent(this, GCMDemoActivity.class);
    resultIntent.putExtra("message", msg);
    resultIntent.putExtra("nick", "Luan");
    resultIntent.putExtra("lat", 34.0274995);
    resultIntent.putExtra("lng", -118.291182);
    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
        resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_gcm2)
        .setContentTitle("Palhunter Notification")
        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
        .setContentText(msg);
    mBuilder.setPriority(Notification.PRIORITY_HIGH);
    mBuilder.setContentIntent(contentIntent);

    Notification notification = mBuilder.build();
    notification.flags |= Notification.FLAG_AUTO_CANCEL
        | Notification.FLAG_SHOW_LIGHTS;
    mNotificationManager.notify(NOTIFICATION_ID, notification);
  }
}
