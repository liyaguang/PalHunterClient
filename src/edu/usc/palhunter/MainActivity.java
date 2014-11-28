package edu.usc.palhunter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.facebook.AppEventsLogger;

import edu.usc.palhunter.data.LocalUserInfo;
import edu.usc.palhunter.ui.navi.BottomNavigateActivity;
import edu.usc.palhunter.ui.sidebar.SideBarActivity;

public class MainActivity extends Activity {

  public final static String EXTRA_MESSAGE = "com.example.androiddemo.MESSAGE";
  private static final String TAG = "MainActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // setContentView(R.layout.activity_main);
    setContentView(R.layout.activity_main);
    calcHash();
    init();
  }

  private void calcHash() {
    PackageInfo info;
    try {
      info = getPackageManager().getPackageInfo("edu.usc.palhunter",
          PackageManager.GET_SIGNATURES);
      for (Signature signature : info.signatures) {
        MessageDigest md = MessageDigest.getInstance("SHA");
        md.update(signature.toByteArray());
        Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
      }
    } catch (NameNotFoundException e) {
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

  }

  private void init() {
    SharedPreferences pref = getPreferences(MODE_PRIVATE);
    String savedMessage = pref.getString(
        getString(R.string.main_activity_saved_message), "");
    // get editor
    EditText editText = (EditText) findViewById(R.id.edit_message);
    editText.setText(savedMessage);
    setUserId();
  }

  private void setUserId() {
    // FIXME
    LocalUserInfo.setUserId(this, 1);
  }

  @Override
  protected void onPause() {
    super.onPause();

    // Logs 'app deactivate' App Event
    AppEventsLogger.deactivateApp(this);
    Log.d(TAG, "App paused");
  }

  @Override
  protected void onStop() {
    super.onStop();
    Log.d(TAG, "App stoped");
  }

  @Override
  protected void onResume() {
    super.onResume();

    // Logs 'install' and 'app activate' App Events
    AppEventsLogger.activateApp(this);
    Log.d(TAG, "App resumed");
  }

  @Override
  protected void onStart() {
    super.onStart();
    Log.d(TAG, "App Started");
    boolean gpsEnabled = checkGPS();
    Log.d(TAG, "GPS Enabled: " + gpsEnabled);
  }

  public void btnCommitClick(View view) {
    getCurrentLocation();

  }

  private boolean checkGPS() {
    // Check GPS
    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    boolean gpsEnabled = locationManager
        .isProviderEnabled(LocationManager.GPS_PROVIDER);
    return gpsEnabled;
  }

  private void getCurrentLocation() {
    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
        0, new LocationListener() {

          @Override
          public void onStatusChanged(String provider, int status, Bundle extras) {

          }

          @Override
          public void onProviderEnabled(String provider) {

          }

          @Override
          public void onProviderDisabled(String provider) {

          }

          @Override
          public void onLocationChanged(Location location) {
            EditText editText = (EditText) findViewById(R.id.edit_message);
            String locText = String.format("Lat: %.6f, lng: %.6f",
                location.getLatitude(), location.getLongitude());
            Log.d(TAG, locText);
            editText.setText(locText);
          }
        });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    MenuInflater inflater = getMenuInflater();
    // inflater.inflate(R.menu.main, menu);
    inflater.inflate(R.menu.main_activity_actions, menu);
    return super.onCreateOptionsMenu(menu);
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

  /**
   * Called when click the button
   * 
   * @param view
   */
  public void sendMessage(View view) {
    Intent intent = new Intent(this, DisplayMessageActivity.class);
    EditText editText = (EditText) findViewById(R.id.edit_message);
    String message = editText.getText().toString();
    // save message using shared preference
    SharedPreferences pref = getPreferences(MODE_PRIVATE);
    SharedPreferences.Editor editor = pref.edit();
    editor.putString(getString(R.string.main_activity_saved_message), message);
    editor.commit();
    intent.putExtra(EXTRA_MESSAGE, message);
    startActivity(intent);
  }

  public void btnOpenMapClick(View view) {
    Intent intent = new Intent(this, MapActivity.class);
    startActivity(intent);
  }

  public void btnLoginClick(View view) {
    Intent intent = new Intent(this, FBLoginActivity.class);
    startActivity(intent);
  }

  public void btnShowUserInfoClick(View view) {
    Intent intent = new Intent(this, UserInfoActivity.class);
    startActivity(intent);
  }

  public void btnGCMTestClick(View view) {
    Intent intent = new Intent(this, GCMDemoActivity.class);
    startActivity(intent);
  }

  public void btnBottomNavClick(View view) {
    Intent intent = new Intent(this, BottomNavigateActivity.class);
    startActivity(intent);
  }

  public void btnSideBarClick(View view) {
    Intent intent = new Intent(this, SideBarActivity.class);
    startActivity(intent);
  }
}
