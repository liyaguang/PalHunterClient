package edu.usc.palhunter.client.main;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import edu.usc.palhunter.R;

public class MapActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
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
		mMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng point) {
				// TODO Auto-generated method stub
				mMap.addMarker(new MarkerOptions()
						.position(point)
						.title("Point")
						.snippet("Population: 4,137,400")
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.marker))
						.draggable(true));
				rectOptions.add(point);
				if (polyline != null) {
					polyline.remove();
				}
				polyline = mMap.addPolyline(rectOptions);
			}
		});
		
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
