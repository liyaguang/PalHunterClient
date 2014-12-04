package edu.usc.palhunter.ui.sidebar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import edu.usc.palhunter.R;
import edu.usc.palhunter.UserInfoActivity.ErrorDialogFragment;
import edu.usc.palhunter.config.Config;
import edu.usc.palhunter.db.LocalUserInfo;
import edu.usc.palhunter.roadnetwork.GeoPoint;
import edu.usc.palhunter.roadnetwork.IGeoPoint;
import edu.usc.palhunter.util.APIRequest;
import edu.usc.palhunter.util.APIRequest.APICallback;

public class HomeFragment extends Fragment implements
    GooglePlayServicesClient.ConnectionCallbacks,
    GooglePlayServicesClient.OnConnectionFailedListener,
    com.google.android.gms.location.LocationListener, SensorEventListener,
    OnClickListener, SnapshotReadyCallback {
  private final static String TAG = "HomeFragement";
  private final static String PREF_KEY = "HomeFragment";

  private FragmentActivity context = null;
  private int userId = 0;

  /** Varaibles related to google map */
  private GoogleMap mMap = null;
  private MapView mMapView = null;
  private CameraPosition cameraPosition = null;
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
  boolean mUpdateLocation = false;

  // Variable for shared preference to store user data
  SharedPreferences mPrefs = null;
  Editor mEditor = null;

  TextView mAddressView = null;
  ProgressBar mActivityIndicator = null;

  // Get back the mutable Polyline
  Polyline polyline = null;

  // Varaible related to trip
  private SensorManager sensorManager;
  List<IGeoPoint> points = null;
  private final static String TRIP_PREF_KEY = "TRIP";
  private Button btnStart = null;
  private TextView tvDistance = null;
  private TextView tvCalories = null;
  private TextView tvSteps = null;
  private TextView tvDuration = null;
  long startTime = 0;
  Timer tripTimer = null;
  float distance = 0;
  int startSteps = 0, currentSteps = 0;
  double calorie = 0;
  int tripId = 0;

  @Override
  public View onCreateView(LayoutInflater inflater,
      @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    if (view != null) {
      ViewGroup parent = (ViewGroup) view.getParent();
      if (parent != null)
        parent.removeView(view);

    }
    view = inflater.inflate(R.layout.home_frg, container, false);
    mMapView = (MapView) view.findViewById(R.id.mapView);
    mMapView.onCreate(savedInstanceState);
    mMapView.onResume();

    try {
      MapsInitializer.initialize(context.getApplicationContext());
    } catch (Exception e) {
      e.printStackTrace();
    }
    initViews(view);
    return view;
  }

  /**
   * Initiate views and related variables
   * 
   * @param v
   */
  private void initViews(View v) {
    // mMap = ((MapFragment) context.getFragmentManager().findFragmentById(
    // R.id.map)).getMap();
    sensorManager = (SensorManager) context
        .getSystemService(Context.SENSOR_SERVICE);
    initMap();
    initEditors();
    initLocationRequest();
    initTextViewAndButton(view);
    initButtonClicks(view);
  }

  private void initTextViewAndButton(View view) {
    // TODO Auto-generated method stub
    tvDuration = (TextView) view.findViewById(R.id.tvDuration);
    tvDistance = (TextView) view.findViewById(R.id.tvDistance);
    tvCalories = (TextView) view.findViewById(R.id.tvCalories);
    tvSteps = (TextView) view.findViewById(R.id.tvSteps);
    btnStart = (Button) view.findViewById(R.id.btnStart);
  }

  /**
   * Associate click method with this fragment
   * 
   * @param view
   */
  private void initButtonClicks(View view) {
    // TODO Auto-generated method stub
    int[] ids = new int[] { R.id.btnStart };
    for (int id : ids) {
      ((Button) view.findViewById(id)).setOnClickListener(this);
    }
  }

  private void initEditors() {
    mPrefs = context.getSharedPreferences(TRIP_PREF_KEY, Context.MODE_PRIVATE);
    mEditor = mPrefs.edit();
  }

  private void initMap() {
    mMap = mMapView.getMap();
    mMap.getUiSettings().setMyLocationButtonEnabled(true);
    mMap.setMyLocationEnabled(true);
    mMap.setOnMapClickListener(new OnMapClickListener() {
      @Override
      public void onMapClick(LatLng point) {
        // TODO Auto-generated method stub
        mMap.addMarker(new MarkerOptions().position(point).title("Point")
            .snippet(point.toString())
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
            .draggable(true));
        rectOptions.add(point);
        if (polyline != null) {
          polyline.remove();
        }
        polyline = mMap.addPolyline(rectOptions);
      }
    });
    if (cameraPosition == null) {
      mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {

          CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(
              location.getLatitude(), location.getLongitude()));
          CameraUpdate zoom = CameraUpdateFactory.zoomTo(18);
          mMap.moveCamera(center);
          mMap.animateCamera(zoom);
          mMap.setOnMyLocationChangeListener(null);
        }
      });
    }
  }

  private void initLocationRequest() {
    mLocationClient = new LocationClient(context, this, this);
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
      int userId = LocalUserInfo.getUserId(context);
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
        data.put("userId", userId);
        param.add(new BasicNameValuePair("data", data.toString()));
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
    if (mUpdateLocation) {
      String msg = "Updated Location: "
          + Double.toString(location.getLatitude()) + ","
          + Double.toString(location.getLongitude());
      Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
      IGeoPoint point = new GeoPoint(location.getLatitude(),
          location.getLongitude());
      // calculate distance
      if (points.size() > 0) {
        double temp = GeoPoint.GetPreciseDistance(
            points.get(points.size() - 1), point);
        distance = (float) (distance + temp);
      }
      points.add(point);
      if (polyline != null) {
        List<LatLng> latLngs = polyline.getPoints();
        latLngs
            .add(new LatLng(location.getLatitude(), location.getLongitude()));
        polyline.setPoints(latLngs);
      }
      UpdateUserLocationTask task = new UpdateUserLocationTask();
      task.execute(location);
    }
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
  public void onAttach(Activity activity) {
    // TODO Auto-generated method stub
    super.onAttach(activity);
    context = (FragmentActivity) activity;
  }

  @Override
  public void onStart() {
    // TODO Auto-generated method stub
    super.onStart();
    mLocationClient.connect();
  }

  @Override
  public void onPause() {
    // TODO Auto-generated method stub

    // save trip data
    saveTripData();
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
    restoreTripData();
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
    mMapView.onDestroy();
  }

  @Override
  public void onDestroyView() {
    // TODO Auto-generated method stub
    super.onDestroyView();
    if (mMap != null) {
      // context.getFragmentManager().beginTransaction()
      // .remove(context.getFragmentManager().findFragmentById(R.id.map))
      // .commit();
      // mMap = null;
    }
  }

  @Override
  public void onLowMemory() {
    // TODO Auto-generated method stub
    super.onLowMemory();
    mMapView.onLowMemory();
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
    if (mUpdateLocation) {
      mUpdateLocation = false;
      mLocationClient.removeLocationUpdates(this);
      btnStart.setText(getString(R.string.start));
      endTrip();
    } else {
      mUpdateLocation = true;
      mLocationClient.requestLocationUpdates(mLocationRequest, this);
      startTrip();
      btnStart.setText(getString(R.string.finish));
    }

  }

  private void saveTripData() {
    // save camera position
    cameraPosition = mMap.getCameraPosition();
    mMapView.onPause();

    JSONArray array = new JSONArray();
    if (points != null) {
      for (IGeoPoint p : points) {
        array.put(p.toJSONObject());
      }
    }
    mEditor.putString("points", array.toString());
    mEditor.putLong("startTime", startTime);
    mEditor.putFloat("distance", distance);
    mEditor.putInt("startSteps", startSteps);
    mEditor.putInt("tripId", tripId);
    mEditor.putBoolean("KEY_UPDATES_ON", mUpdateLocation);
    mEditor.commit();

  }

  private void restoreTripData() {
    // Button text
    if (mPrefs.contains("KEY_UPDATES_ON")) {
      mUpdateLocation = mPrefs.getBoolean("KEY_UPDATES_ON", false);
      // Otherwise, turn off location updates
    } else {
      mEditor.putBoolean("KEY_UPDATES_ON", false);
      mEditor.commit();
    }
    points = new ArrayList<IGeoPoint>();
    try {
      JSONArray array = new JSONArray(mPrefs.getString("points", "[]"));
      for (int i = 0; i < array.length(); ++i) {
        points.add(new GeoPoint((JSONObject) array.get(i)));
      }
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    startTime = mPrefs.getLong("startTime", System.currentTimeMillis());
    startSteps = mPrefs.getInt("startSteps", 0);
    distance = mPrefs.getFloat("distance", 0);
    tripId = mPrefs.getInt("tripId", 0);

    // Google map
    mMapView.onResume();
    if (cameraPosition != null) {
      mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    drawTrip(points);
    if (mUpdateLocation) {
      btnStart.setText(getString(R.string.finish));
    } else {
      btnStart.setText(getString(R.string.start));
    }
  }

  private void drawTrip(List<IGeoPoint> points) {
    for (IGeoPoint point : points) {
      LatLng latLng = new LatLng(point.getLat(), point.getLng());
      // mMap.addMarker(new MarkerOptions().position(latLng).title("Point")
      // .snippet(point.toString())
      // .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
      // .draggable(true));
      rectOptions.add(latLng);
      if (polyline != null) {
        polyline.remove();
      }
      polyline = mMap.addPolyline(rectOptions);
    }
  }

  private void clearMap() {
    polyline.remove();
  }

  private void startTrip() {
    int userId = LocalUserInfo.getUserId(context);
    String api = "StartTrip";
    JSONObject params = new JSONObject();
    try {
      params.put("userId", userId);
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    APIRequest.post(api, params, new APICallback() {

      @Override
      public void process(String result) {
        try {
          JSONObject obj = new JSONObject(result);
          tripId = obj.getInt("tripId");
        } catch (JSONException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    });
    points = new ArrayList<IGeoPoint>();
    startTime = System.currentTimeMillis();
    tripTimer = new Timer();
    tripTimer.schedule(new UpdateTripTask(), 100, 1000);
    Sensor countSensor = sensorManager
        .getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

    if (countSensor != null) {
      sensorManager.registerListener(this, countSensor,
          SensorManager.SENSOR_DELAY_UI);
    } else {
      Toast.makeText(context, "Count sensor not available!", Toast.LENGTH_LONG)
          .show();
    }
  }

  private void endTrip() {
    int numPoints = points.size();
    if (tripTimer != null) {
      tripTimer.cancel();
    }
    // Report trip to server
    String api = "EndTrip";
    int userId = LocalUserInfo.getUserId(context);
    String userName = LocalUserInfo.getUserName(context);
    String info = String.format("%s's awesome trip.", userName);
    long duration = System.currentTimeMillis() - startTime;
    double calories = 60 * distance / 1000 * 1.036;
    JSONObject data = new JSONObject();
    JSONObject params = new JSONObject();
    try {
      data.put("userId", userId);
      data.put("tripId", tripId);
      data.put("steps", currentSteps - startSteps);
      data.put("calorie", calories);
      data.put("distance", distance);
      data.put("duration", duration);
      data.put("info", info);
      params.put("data", data);
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    APIRequest.post(api, params, null);
    shareTrip();
    // mMap.snapshot(this);
    Log.i(TAG, String.format("Collected %d points.", numPoints));
    clearTrip();
  }

  private void shareTrip() {
    // TODO Auto-generated method stub
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    view.setDrawingCacheEnabled(true);

    showShareDialog();

  }

  private void clearTrip() {
    // TODO Auto-generated method stub
    points = null;
    distance = 0;
    calorie = 0;
    startSteps = 0;
    tripId = 0;

    if (sensorManager != null) {
      Sensor countSensor = sensorManager
          .getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
      if (countSensor != null) {
        sensorManager.unregisterListener(this, countSensor);
      }
    }
  }

  class UpdateTripTask extends TimerTask {

    public void updateDuration() {
      long millis = System.currentTimeMillis() - startTime;
      int seconds = (int) (millis / 1000);
      int minutes = seconds / 60;
      int hours = minutes / 60;
      minutes %= 60;
      seconds %= 60;
      tvDuration.setText(String.format("%02d:%02d:%02d", hours, minutes,
          seconds));

    }

    public void updateSteps() {
      int stepCount = currentSteps - startSteps;
      tvSteps.setText(String.format("%d", stepCount));
    }

    public void updateDistance() {
      tvDistance.setText(String.format("%.0f", distance));
    }

    public void updateCalories() {
      double calories = 60 * distance / 1000 * 1.036;
      tvCalories.setText(String.format("%.2f", calories));
    }

    public void run() {
      context.runOnUiThread(new Runnable() {
        @Override
        public void run() {
          // TODO Auto-generated method stub
          updateSteps();
          updateDuration();
          updateCalories();
          updateDistance();
        }
      });
    }
  }

  @Override
  public void onSensorChanged(SensorEvent event) {
    // TODO Auto-generated method stub
    currentSteps = (int) event.values[0];
    if (startSteps == 0) {
      startSteps = currentSteps;
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {
    // TODO Auto-generated method stub

  }

  private void showShareDialog() {
    new AlertDialog.Builder(getActivity())
        .setTitle(R.string.str_share_with_your_friends)
        .setMessage(R.string.str_share_with_your_friends_msg)
        .setNegativeButton(android.R.string.cancel,
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
              }
            })
        .setPositiveButton(android.R.string.ok,
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
                Bitmap bitmap = getBitmapFromView(view);
                shareImage(bitmap);
              }
            }).setCancelable(false).create().show();
  }

  @Override
  public void onSnapshotReady(Bitmap snapshot) {
    // TODO Auto-generated method stub
    // TODO Auto-generated method stub
    shareImage(snapshot);
  }

  public void shareImage(Bitmap bitmap) {

    OutputStream fout = null;

    String filePath = System.currentTimeMillis() + ".jpeg";
    try {
      fout = context.openFileOutput(filePath, Context.MODE_WORLD_READABLE);

      // Write the string to the file
      bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fout);
      fout.flush();
      fout.close();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      Log.d("ImageCapture", "FileNotFoundException");
      Log.d("ImageCapture", e.getMessage());
      filePath = "";
    } catch (IOException e) {
      // TODO Auto-generated catch block
      Log.d("ImageCapture", "IOException");
      Log.d("ImageCapture", e.getMessage());
      filePath = "";
    }
    openShareImageDialog(filePath);

  }

  public void openShareImageDialog(String filePath) {
    File file = context.getFileStreamPath(filePath);

    if (!filePath.equals("")) {
      final ContentValues values = new ContentValues(2);
      values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
      values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
      final Uri contentUriFile = context.getContentResolver().insert(
          MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

      final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
      intent.setType("image/jpeg");
      intent.putExtra(android.content.Intent.EXTRA_STREAM, contentUriFile);
      startActivity(Intent.createChooser(intent, "Share Your Trip"));
    } else {
      // This is a custom class I use to show dialogs...simply replace this with
      // whatever you want to show an error message, Toast, etc.
      // DialogUtilities.showOkDialogWithText(this, R.string.shareImageFailed);
    }
  }

  public static Bitmap getBitmapFromView(View view) {
    // Define a bitmap with the same size as the view
    Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(),
        view.getHeight(), Bitmap.Config.ARGB_8888);
    // Bind a canvas to it
    Canvas canvas = new Canvas(returnedBitmap);
    // Get the view's background
    Drawable bgDrawable = view.getBackground();
    if (bgDrawable != null)
      // has background drawable, then draw it on the canvas
      bgDrawable.draw(canvas);
    else
      // does not have background drawable, then draw white background on the
      // canvas
      canvas.drawColor(Color.WHITE);
    // draw the view on the canvas
    view.draw(canvas);
    // return the bitmap
    return returnedBitmap;
  }
}
