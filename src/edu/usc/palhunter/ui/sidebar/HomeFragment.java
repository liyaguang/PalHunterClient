package edu.usc.palhunter.ui.sidebar;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import edu.usc.palhunter.R;
import edu.usc.palhunter.UserInfoActivity.ErrorDialogFragment;
import edu.usc.palhunter.config.Config;

public class HomeFragment extends Fragment implements
    GooglePlayServicesClient.ConnectionCallbacks,
    GooglePlayServicesClient.OnConnectionFailedListener,
    com.google.android.gms.location.LocationListener, OnClickListener {
  private final static String TAG = "HomeFragement";
  private final static String PREF_KEY = "HomeFragment";
  private FragmentActivity context = null;
  private GoogleMap mMap = null;
  private View view;
  PolylineOptions rectOptions = new PolylineOptions().color(Color.BLUE);

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

  private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
  // Define an object that holds accuracy and frequency parameters
  LocationRequest mLocationRequest;
  boolean mUpdatesRequested;

  // Variable for shared preference to store user data
  SharedPreferences mPrefs = null;
  Editor mEditor = null;

  TextView mAddressView = null;
  ProgressBar mActivityIndicator = null;

  // Get back the mutable Polyline

  Polyline polyline = null;

  @Override
  public View onCreateView(LayoutInflater inflater,
      @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    if (view != null) {
      ViewGroup parent = (ViewGroup) view.getParent();
      if (parent != null)
        parent.removeView(view);

    }
    view = inflater.inflate(R.layout.home_frg, container, false);
    getViews(view);
    initButtons(view);
    initLocationRequest();
    // return super.onCreateView(inflater, container, savedInstanceState);
    return view;
  }

  private void initButtons(View view) {
    // TODO Auto-generated method stub
    int[] ids = new int[] { R.id.btnStart };
    for (int id : ids) {
      ((Button) view.findViewById(id)).setOnClickListener(this);
    }
  }

  @Override
  public void onAttach(Activity activity) {
    // TODO Auto-generated method stub
    super.onAttach(activity);
    context = (FragmentActivity) activity;

  }

  private void getViews(View v) {
    mMap = ((MapFragment) context.getFragmentManager().findFragmentById(
        R.id.map)).getMap();
    mMap.getUiSettings().setMyLocationButtonEnabled(true);
    mMap.setMyLocationEnabled(true);
    mMap.setOnMapClickListener(new OnMapClickListener() {
      @Override
      public void onMapClick(LatLng point) {
        // TODO Auto-generated method stub
        mMap.addMarker(new MarkerOptions().position(point).title("Point")
            .snippet("Population: 4,137,400")
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
            .draggable(true));
        rectOptions.add(point);
        if (polyline != null) {
          polyline.remove();
        }
        polyline = mMap.addPolyline(rectOptions);
      }
    });
    mPrefs = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
    mEditor = mPrefs.edit();
  }

  private void initLocationRequest() {
    mLocationClient = new LocationClient(context, this, this);
    mUpdatesRequested = false;
    mLocationRequest = LocationRequest.create();
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    mLocationRequest.setInterval(UPDATE_INTERVAL);
    mLocationRequest.setInterval(FASTEST_INTERVAL);
  }

  private boolean servicesConnected() {
    // Check that Google Play services is available
    int resultCode = GooglePlayServicesUtil
        .isGooglePlayServicesAvailable(context);
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
          context, CONNECTION_FAILURE_RESOLUTION_REQUEST);

      // If Google Play services can provide an error dialog
      if (errorDialog != null) {
        // Create a new DialogFragment for the error dialog
        ErrorDialogFragment errorFragment = new ErrorDialogFragment();
        // Set the dialog in the DialogFragment
        errorFragment.setDialog(errorDialog);
        // Show the error dialog in the DialogFragment
        errorFragment.show(context.getFragmentManager(), "Location Updates");
      }
    }
    return false;
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
        data.put("lat", loc.getLatitude());
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

  @Override
  public void onLocationChanged(Location location) {
    // TODO Auto-generated method stub
    String msg = "Updated Location: " + Double.toString(location.getLatitude())
        + "," + Double.toString(location.getLongitude());
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    // UpdateUserLocationTask task = new UpdateUserLocationTask();
    // task.execute(location);
  }

  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {
    // TODO Auto-generated method stub
    // TODO Auto-generated method stub
    /*
     * Google Play services can resolve some errors it detects. If the error has
     * a resolution, try sending an Intent to start a Google Play services
     * activity that can resolve error.
     */
    if (connectionResult.hasResolution()) {
      try {
        // Start an Activity that tries to resolve the error
        connectionResult.startResolutionForResult(context,
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
      Toast.makeText(context, "No resolution avaible!", Toast.LENGTH_SHORT)
          .show();
    }
  }

  @Override
  public void onConnected(Bundle arg0) {
    // TODO Auto-generated method stub
    Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();

  }

  @Override
  public void onDisconnected() {
    // TODO Auto-generated method stub
    Toast.makeText(context, "Disconnected. Please re-connect.",
        Toast.LENGTH_SHORT).show();
  }

  /** Method for managing life cycle */
  @Override
  public void onStart() {
    // TODO Auto-generated method stub
    super.onStart();
    mLocationClient.connect();
  }

  @Override
  public void onPause() {
    // TODO Auto-generated method stub
    mEditor.putBoolean("KEY_UPDATES_ON", mUpdatesRequested);
    mEditor.commit();
    super.onPause();
  }

  @Override
  public void onResume() {
    // TODO Auto-generated method stub
    super.onResume();
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
  }

  @Override
  public void onStop() {
    // TODO Auto-generated method stub
    mLocationClient.disconnect();
    super.onStop();
  }

  @Override
  public void onDestroy() {
    // TODO Auto-generated method stub
    super.onDestroy();
  }

  @Override
  public void onDestroyView() {
    // TODO Auto-generated method stub
    super.onDestroyView();
    if (mMap != null) {
      context.getFragmentManager().beginTransaction()
          .remove(context.getFragmentManager().findFragmentById(R.id.map))
          .commit();
      mMap = null;
    }
  }

  @Override
  public void onClick(View v) {
    // TODO Auto-generated method stub
    switch (v.getId()) {
    case R.id.btnStart:
      btnStartClick(v);
      break;
    }
  }

  private void btnStartClick(View v) {
    // TODO Auto-generated method stub
    mUpdatesRequested = true;
    if (mUpdatesRequested) {
      mLocationClient.requestLocationUpdates(mLocationRequest, this);
    }
  }
}
