package edu.usc.palhunter.client.main;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

import edu.usc.palhunter.R;
import edu.usc.palhunter.client.config.Config;
import edu.usc.util.Utility;

public class UserInfoActivity extends Activity implements
    GooglePlayServicesClient.ConnectionCallbacks,
    GooglePlayServicesClient.OnConnectionFailedListener,
    com.google.android.gms.location.LocationListener {

  private static final String TAG = "UserInfoActivity";
  LocationClient mLocationClient = null;
  Location mCurrentLocation = null;

  // Milliseconds per second
  private static final int MILLISECONDS_PER_SECOND = 1000;
  // Update frequency in seconds
  public static final int UPDATE_INTERVAL_IN_SECONDS = 10;
  // Update frequency in milliseconds
  private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND
      * UPDATE_INTERVAL_IN_SECONDS;
  // The fastest update frequency, in seconds
  private static final int FASTEST_INTERVAL_IN_SECONDS = 5;
  // A fast frequency ceiling in milliseconds
  private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND
      * FASTEST_INTERVAL_IN_SECONDS;

  // Define an object that holds accuracy and frequency parameters
  LocationRequest mLocationRequest;
  boolean mUpdatesRequested;

  // Variable for shared preference to store user data
  SharedPreferences mPrefs = null;
  Editor mEditor = null;

  TextView mAddressView = null;
  ProgressBar mActivityIndicator = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_info);

    mPrefs = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
    mEditor = mPrefs.edit();

    // init
    mAddressView = (TextView) findViewById(R.id.tv_address);
    mActivityIndicator = (ProgressBar) findViewById(R.id.address_progress);
    // Set location related things
    initLocationRequest();

  }

  private void initLocationRequest() {
    mLocationClient = new LocationClient(this, this, this);
    mUpdatesRequested = true;
    mLocationRequest = LocationRequest.create();
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    mLocationRequest.setInterval(UPDATE_INTERVAL);
    mLocationRequest.setInterval(FASTEST_INTERVAL);
  }

  @Override
  protected void onStart() {
    super.onStart();
    // Connect the client.
    mLocationClient.connect();
    Log.i(TAG, "location client connected");
  }

  @Override
  protected void onPause() {
    // Save the current setting for updates
    mEditor.putBoolean("KEY_UPDATES_ON", mUpdatesRequested);
    mEditor.commit();
    super.onPause();
  }

  @Override
  protected void onResume() {
    /*
     * Get any previous setting for location updates Gets "false" if an error
     * occurs
     */
    if (mPrefs.contains("KEY_UPDATES_ON")) {
      mUpdatesRequested = mPrefs.getBoolean("KEY_UPDATES_ON", false);

      // Otherwise, turn off location updates
    } else {
      mEditor.putBoolean("KEY_UPDATES_ON", false);
      mEditor.commit();
    }
    super.onResume();
  }

  @Override
  protected void onStop() {
    mLocationClient.disconnect();
    Log.i(TAG, "location client stoped");
    super.onStop();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.user_info, menu);
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

  // Global constants
  /**
   * Define a request code to send to Google Play services This code is returned
   * in Activity.onActivityResult
   */
  private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

  // Define a DialogFragment that displays the error dialog
  public static class ErrorDialogFragment extends DialogFragment {
    // Global field to contain the error dialog
    private Dialog mDialog;

    // Default constructor. Sets the dialog field to null
    public ErrorDialogFragment() {
      super();
      mDialog = null;
    }

    // Set the dialog to display
    public void setDialog(Dialog dialog) {
      mDialog = dialog;
    }

    // Return a Dialog to the DialogFragment.
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      return mDialog;
    }
  }

  private boolean servicesConnected() {
    // Check that Google Play services is available
    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    // If Google Play services is available
    if (ConnectionResult.SUCCESS == resultCode) {
      // In debug mode, log the status
      Log.d("Location Updates", "Google Play services is available.");
      // Continue
      return true;
      // Google Play services was not available for some reason.
      // resultCode holds the error code.
    } else {
      // Get the error dialog from Google Play services
      Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode,
          this, CONNECTION_FAILURE_RESOLUTION_REQUEST);

      // If Google Play services can provide an error dialog
      if (errorDialog != null) {
        // Create a new DialogFragment for the error dialog
        ErrorDialogFragment errorFragment = new ErrorDialogFragment();
        // Set the dialog in the DialogFragment
        errorFragment.setDialog(errorDialog);
        // Show the error dialog in the DialogFragment
        errorFragment.show(getFragmentManager(), "Location Updates");
      }
    }
    return false;
  }

  void showErrorDialog(int code) {
    GooglePlayServicesUtil.getErrorDialog(code, this,
        CONNECTION_FAILURE_RESOLUTION_REQUEST).show();
  }

  private class GetUserInfoTask extends AsyncTask<Void, Void, String> {

    String info = "";

    @Override
    protected String doInBackground(Void... params) {
      // TODO Auto-generated method stub
      try {
        // TODO Auto-generated method stub
        String addr = String.format("%s/GetUserInfo?userId=1",
            Config.getApiBaseAddr());
        // addr = "http://www.google.com/";
        URL url = new URL(addr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        String result2 = Utility.streamToString(conn.getInputStream());
        JSONObject obj = new JSONObject(result2);
        info = obj.getString("userName");
        conn.disconnect();
      } catch (MalformedURLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return info;
    }

    protected void onPostExecute(String result) {
      showInfo(result);
    }
  }

  private class GetAddressTask extends AsyncTask<Location, Void, String> {

    Context mContext;

    public GetAddressTask(Context context) {
      super();
      mContext = context;
    }

    @Override
    protected String doInBackground(Location... params) {
      // TODO Auto-generated method stub
      Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
      // Get the current location from the input parameter list
      Location loc = params[0];
      // Create a list to contain the result address
      List<Address> addresses = null;
      try {
        /*
         * Return 1 address.
         */
        addresses = geocoder.getFromLocation(loc.getLatitude(),
            loc.getLongitude(), 1);
      } catch (IOException e1) {
        Log.e("LocationSampleActivity", "IO Exception in getFromLocation()");
        e1.printStackTrace();
        return ("IO Exception trying to get address");
      } catch (IllegalArgumentException e2) {
        // Error message to post in the log
        String errorString = "Illegal arguments "
            + Double.toString(loc.getLatitude()) + " , "
            + Double.toString(loc.getLongitude())
            + " passed to address service";
        Log.e("LocationSampleActivity", errorString);
        e2.printStackTrace();
        return errorString;
      }
      // If the reverse geocode returned an address
      if (addresses != null && addresses.size() > 0) {
        // Get the first address
        Address address = addresses.get(0);
        /*
         * Format the first line of address (if available), city, and country
         * name.
         */
        String addressText = String.format("%s, %s, %s",
        // If there's a street address, add it
            address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0)
                : "",
            // Locality is usually a city
            address.getLocality(),
            // The country of the address
            address.getCountryName());
        // Return the text
        return addressText;
      } else {
        return "No address found";
      }
    }

    @Override
    protected void onPostExecute(String result) {
      // TODO Auto-generated method stub
      super.onPostExecute(result);
      // Set activity indicator visibility to "gone"
      mActivityIndicator.setVisibility(View.GONE);
      // Display the results of the lookup.
      mAddressView.setText(result);
    }

  }

  private class UpdateUserLocationTask extends AsyncTask<Location, Void, Void> {

    @Override
    protected Void doInBackground(Location... params) {
      // TODO Auto-generated method stub
      Location loc = params[0];

      try {
        String addr = String.format("%s/LocationUpdate",
            Config.getApiBaseAddr());
        // addr = "http://www.google.com/";
        URL url = new URL(addr);
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(addr);
        List<NameValuePair> param = new ArrayList<NameValuePair>();
        JSONObject data = new JSONObject();
        data.put("lat",loc.getLatitude());
        data.put("lng", loc.getLongitude());
        data.put("userId", 0);
        param.add(new BasicNameValuePair("info", data.toString()));
        httpPost.setEntity(new UrlEncodedFormEntity(param));
        HttpResponse response = client.execute(httpPost);
        // EntityUtils.toString(response.getEntity());
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return null;
    }

  }

  /**
   * Show info in the textview
   * 
   * @param info
   */
  public void showInfo(String info) {
    TextView tv = (TextView) findViewById(R.id.tv_show_user_info);
    tv.setText(info);
  }

  private void getCurrentLocation() {
    boolean connected = servicesConnected();
    mCurrentLocation = mLocationClient.getLastLocation();
    String locText = String.format("Lat: %.6f, lng: %.6f",
        mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
    Log.d(TAG, locText);
    showInfo(locText);
    // LocationManager locationManager = (LocationManager)
    // getSystemService(Context.LOCATION_SERVICE);
    // locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
    // 0,
    // 0, new LocationListener() {
    //
    // @Override
    // public void onStatusChanged(String provider, int status, Bundle extras) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // @Override
    // public void onProviderEnabled(String provider) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // @Override
    // public void onProviderDisabled(String provider) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // @Override
    // public void onLocationChanged(Location location) {
    // // TODO Auto-generated method stub
    // String locText = String.format("Lat: %.6f, lng: %.6f",
    // location.getLatitude(), location.getLongitude());
    // Log.d(TAG, locText);
    // showInfo(locText);
    // }
    // });
  }

  public void showUserInfoClick(View v) {
    new GetUserInfoTask().execute();
  }

  public void showUserLocationClick(View v) {
    getCurrentLocation();
  }

  public void getUserAddressClick(View v) {
    // Ensure that a Geocoder services is available
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD
        && Geocoder.isPresent()) {
      // Show the activity indicator
      mActivityIndicator.setVisibility(View.VISIBLE);
      /*
       * Reverse geocoding is long-running and synchronous. Run it on a
       * background thread. Pass the current location to the background task.
       * When the task finishes, onPostExecute() displays the address.
       */
      (new GetAddressTask(this)).execute(mCurrentLocation);
    }
  }

  public void updateLocationClick(View v) {
    UpdateUserLocationTask task = new UpdateUserLocationTask();
  }
  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {
    // TODO Auto-generated method stub
    /*
     * Google Play services can resolve some errors it detects. If the error has
     * a resolution, try sending an Intent to start a Google Play services
     * activity that can resolve error.
     */
    if (connectionResult.hasResolution()) {
      try {
        // Start an Activity that tries to resolve the error
        connectionResult.startResolutionForResult(this,
            CONNECTION_FAILURE_RESOLUTION_REQUEST);
        /*
         * Thrown if Google Play services canceled the original PendingIntent
         */
      } catch (IntentSender.SendIntentException e) {
        // Log the error
        e.printStackTrace();
      }
    } else {
      /*
       * If no resolution is available, display a dialog to the user with the
       * error.
       */
      showErrorDialog(connectionResult.getErrorCode());
      Toast.makeText(this, "No resolution avaible!", Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public void onConnected(Bundle dataBundle) {
    // TODO Auto-generated method stub
    // Display the connection status
    Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
    if (mUpdatesRequested) {
      mLocationClient.requestLocationUpdates(mLocationRequest, this);
    }
  }

  @Override
  public void onDisconnected() {
    // TODO Auto-generated method stub
    // Display the connection status
    Toast
        .makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT)
        .show();
  }

  @Override
  public void onLocationChanged(Location location) {
    String msg = "Updated Location: " + Double.toString(location.getLatitude())
        + "," + Double.toString(location.getLongitude());
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    UpdateUserLocationTask task = new UpdateUserLocationTask();
    task.execute(location);
  }
}
