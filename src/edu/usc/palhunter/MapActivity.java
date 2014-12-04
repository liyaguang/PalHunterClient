package edu.usc.palhunter;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapActivity extends Activity {

  private double lat, lng;

  private String nick;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.map_frg);
    lat = getIntent().getDoubleExtra("lat", 34.0226901);
    lng = getIntent().getDoubleExtra("lng", -118.285117);
    nick = getIntent().getStringExtra("nick");
    init();
  }

  private GoogleMap mMap = null;
  PolylineOptions rectOptions = new PolylineOptions().color(Color.BLUE);

  // Get back the mutable Polyline

  Polyline polyline = null;
  private static final LatLng MELBOURNE = new LatLng(-37.813, 144.962);

  private void init() {
    mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
        .getMap();

    mMap.getUiSettings().setMyLocationButtonEnabled(true);
    mMap.setMyLocationEnabled(true);

    LatLng position = new LatLng(lat, lng);
    CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(lat, lng));
    CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
    mMap.moveCamera(center);
    mMap.animateCamera(zoom);
    Marker marker = mMap.addMarker(new MarkerOptions().position(position)
        .title(nick).snippet(nick)
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
    marker.showInfoWindow();
  }

  private void OnMapClickListener(LatLng postion) {
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.map, menu);
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
