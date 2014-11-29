package edu.usc.palhunter;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class GoogleMapFragment extends Fragment {
  private GoogleMap mMap = null;
  PolylineOptions rectOptions = new PolylineOptions().color(Color.BLUE);

  // Get back the mutable Polyline

  Polyline polyline = null;
  private FragmentActivity context = null;
  private static final LatLng MELBOURNE = new LatLng(-37.813, 144.962);

  @Override
  public View onCreateView(LayoutInflater inflater,
      @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    View v = inflater.inflate(R.layout.map_frg, container, false);
    getViews(v);
    return v;
    // return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override
  public void onAttach(Activity activity) {
    // TODO Auto-generated method stub
    super.onAttach(activity);
    // context = (FragmentActivity) activity;
  }

  private void getViews(View v) {
    // (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
    mMap = ((SupportMapFragment) getActivity().getSupportFragmentManager()
        .findFragmentById(R.id.map)).getMap();
    mMap.getUiSettings().setMyLocationButtonEnabled(true);
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
  }

  @Override
  public void onDestroyView() {
    // TODO Auto-generated method stub
    super.onDestroyView();
    if (mMap != null) {
      getActivity()
          .getSupportFragmentManager()
          .beginTransaction()
          .remove(
              getActivity().getSupportFragmentManager().findFragmentById(
                  R.id.map)).commit();
      mMap = null;
    }

  }

  public GoogleMap getMap() {
    return mMap;
  }
}
