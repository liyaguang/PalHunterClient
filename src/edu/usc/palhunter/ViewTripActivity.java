package edu.usc.palhunter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.usc.palhunter.util.APIRequest;
import edu.usc.palhunter.util.APIRequest.APICallback;

public class ViewTripActivity extends Activity {

  int tripId = 0;
  JSONObject jsonTrip = null;
  JSONArray points = null;
  TextView tvDuration;
  TextView tvDistance;
  TextView tvCalorie;
  TextView tvSteps;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.disp_trip_frg);
    initView();
    tripId = getIntent().getIntExtra("tripId", 0);
    getTrip();
  }

  private void initView() {
    // TODO Auto-generated method stub
    tvDuration = (TextView) findViewById(R.id.tvDuration);
    tvDistance = (TextView) findViewById(R.id.tvDistance);
    tvCalorie = (TextView) findViewById(R.id.tvCalories);
    tvSteps = (TextView) findViewById(R.id.tvSteps);
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.view_trip, menu);
    return true;
  }

  private void refreshContent() {
    Toast.makeText(this, "" + tripId, Toast.LENGTH_SHORT).show();
    if (jsonTrip != null) {
      try {
        long millis = jsonTrip.getLong("duration");
        int steps = jsonTrip.getInt("steps");
        double calorie = jsonTrip.getDouble("calorie");
        double distance = jsonTrip.getDouble("distance");
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        int hours = minutes / 60;
        minutes %= 60;
        seconds %= 60;
        tvDuration.setText(String.format("%02d:%02d:%02d", hours, minutes,
            seconds));
        tvSteps.setText("" + steps);
        tvCalorie.setText(String.format("%.2f", calorie));
        tvDistance.setText(String.format("%.0f", distance));
      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }
  }

  private void getTrip() {
    String api = "GetTrip";
    JSONObject params = new JSONObject();
    try {
      params.put("tripId", tripId);
      APIRequest.get(api, params, new APICallback() {
        @Override
        public void process(String result) {
          // TODO Auto-generated method stub
          try {
            jsonTrip = new JSONObject(result);
            points = jsonTrip.getJSONArray("points");
            refreshContent();
          } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      });
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
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

  @Override
  protected void onResume() {
    // TODO Auto-generated method stub
    super.onResume();
  }

  @Override
  protected void onPause() {
    // TODO Auto-generated method stub
    super.onPause();
  }

  @Override
  public void onLowMemory() {
    // TODO Auto-generated method stub
    super.onLowMemory();
  }

  @Override
  protected void onDestroy() {
    // TODO Auto-generated method stu
    super.onDestroy();
  }
}
