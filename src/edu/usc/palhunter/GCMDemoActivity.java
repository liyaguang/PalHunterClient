package edu.usc.palhunter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import edu.usc.palhunter.data.LocalUserInfo;
import edu.usc.palhunter.util.APIRequest;

public class GCMDemoActivity extends Activity {

  public static final String EXTRA_MESSAGE = "message";
  public static final String PROPERTY_REG_ID = "registration_id";
  private static final String PROPERTY_APP_VERSION = "appVersion";
  private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

  /**
   * Substitute you own sender ID here. This is the project number you got from
   * the API Console, as described in "Getting Started."
   */
  String SENDER_ID = "538955661873";

  /**
   * Tag used on log messages.
   */
  static final String TAG = "GCMDemo";

  TextView mDisplay;
  GoogleCloudMessaging gcm;
  AtomicInteger msgId = new AtomicInteger();
  SharedPreferences prefs;
  Context context;

  String regid;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.gcmdemo);
    mDisplay = (TextView) findViewById(R.id.tvDisplay);

    context = getApplicationContext();

    // Check device for Play Services APK. If check succeeds, proceed with
    // GCM registration.
    if (checkPlayServices()) {
      gcm = GoogleCloudMessaging.getInstance(this);
      regid = getRegistrationId(context);

      if (regid.isEmpty()) {
        registerInBackground();
      }
    } else {
      Log.i(TAG, "No valid Google Play Services APK found.");
    }
    mDisplay.setText(getIntent().getStringExtra("message"));
  }
  @Override
  protected void onNewIntent(Intent intent) {
    // TODO Auto-generated method stub
    super.onNewIntent(intent);
    mDisplay = (TextView) findViewById(R.id.tvDisplay);
//    mDisplay.setText(getIntent().getStringExtra("message"));
  }

  /**
   * Gets the current registration ID for application on GCM service.
   * <p>
   * If result is empty, the app needs to register.
   * 
   * @return registration ID, or empty string if there is no existing
   *         registration ID.
   */
  private String getRegistrationId(Context context) {
    final SharedPreferences prefs = getGCMPreferences(context);
    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
    if (registrationId.isEmpty()) {
      Log.i(TAG, "Registration not found.");
      return "";
    }
    // Check if app was updated; if so, it must clear the registration ID
    // since the existing regID is not guaranteed to work with the new
    // app version.
    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
        Integer.MIN_VALUE);
    int currentVersion = getAppVersion(context);
    if (registeredVersion != currentVersion) {
      Log.i(TAG, "App version changed.");
      return "";
    }
    return registrationId;
  }

  /**
   * @return Application's {@code SharedPreferences}.
   */
  private SharedPreferences getGCMPreferences(Context context) {
    // This sample app persists the registration ID in shared preferences, but
    // how you store the regID in your app is up to you.
    return getSharedPreferences(GCMDemoActivity.class.getSimpleName(),
        Context.MODE_PRIVATE);
  }

  /**
   * @return Application's version code from the {@code PackageManager}.
   */
  private static int getAppVersion(Context context) {
    try {
      PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
          context.getPackageName(), 0);
      return packageInfo.versionCode;
    } catch (NameNotFoundException e) {
      // should never happen
      throw new RuntimeException("Could not get package name: " + e);
    }
  }

  /**
   * Registers the application with GCM servers asynchronously.
   * <p>
   * Stores the registration ID and app versionCode in the application's shared
   * preferences.
   */
  private void registerInBackground() {
    new AsyncTask<Void, Void, String>() {
      @Override
      protected String doInBackground(Void... params) {
        String msg = "";
        try {
          if (gcm == null) {
            gcm = GoogleCloudMessaging.getInstance(context);
          }
          regid = gcm.register(SENDER_ID);
          msg = "Device registered, registration ID=" + regid;

          // You should send the registration ID to your server over HTTP,
          // so it can use GCM/HTTP or CCS to send messages to your app.
          // The request to your server should be authenticated if your app
          // is using accounts.
          sendRegistrationIdToBackend();

          // For this demo: we don't need to send it because the device
          // will send upstream messages to a server that echo back the
          // message using the 'from' address in the message.

          // Persist the regID - no need to register again.
          storeRegistrationId(context, regid);
        } catch (IOException ex) {
          msg = "Error :" + ex.getMessage();
          // If there is an error, don't just keep trying to register.
          // Require the user to click a button again, or perform
          // exponential back-off.
        }
        return msg;
      }

      @Override
      protected void onPostExecute(String msg) {
        mDisplay.append(msg + "\n");
      }

    }.execute(null, null, null);
  }

  /**
   * Stores the registration ID and app versionCode in the application's
   * {@code SharedPreferences}.
   * 
   * @param context
   *          application's context.
   * @param regId
   *          registration ID
   */
  private void storeRegistrationId(Context context, String regId) {
    final SharedPreferences prefs = getGCMPreferences(context);
    int appVersion = getAppVersion(context);
    Log.i(TAG, "Saving regId on app version " + appVersion);
    Log.i(TAG, "regId: " + regId);
    SharedPreferences.Editor editor = prefs.edit();
    editor.putString(PROPERTY_REG_ID, regId);
    editor.putInt(PROPERTY_APP_VERSION, appVersion);
    editor.commit();
  }

  /**
   * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
   * or CCS to send messages to your app. Not needed for this demo since the
   * device sends upstream messages to a server that echoes back the message
   * using the 'from' address in the message.
   */
  private void sendRegistrationIdToBackend() {
    // Your implementation here.
    String api = "SetRid";
    int userId = LocalUserInfo.getUserId(this);
    JSONObject params = new JSONObject();
    try {
      params.put("rid", regid);
      params.put("userId", userId);
      APIRequest.post(api, params, null);
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Check the device to make sure it has the Google Play Services APK. If it
   * doesn't, display a dialog that allows users to download the APK from the
   * Google Play Store or enable it in the device's system settings.
   */
  private boolean checkPlayServices() {
    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    if (resultCode != ConnectionResult.SUCCESS) {
      if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
        GooglePlayServicesUtil.getErrorDialog(resultCode, this,
            PLAY_SERVICES_RESOLUTION_REQUEST).show();
      } else {
        Log.i(TAG, "This device is not supported.");
        finish();
      }
      return false;
    }
    return true;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.gcmdemo, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
