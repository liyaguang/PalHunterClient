package edu.usc.palhunter.ui.sidebar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import edu.usc.palhunter.R;
import edu.usc.palhunter.ViewTripActivity;
import edu.usc.palhunter.db.LocalUserInfo;
import edu.usc.palhunter.db.Trip;
import edu.usc.palhunter.util.APIRequest;
import edu.usc.palhunter.util.APIRequest.APICallback;

public class TripsFragment extends ListFragment {
  private List<Trip> trips = new ArrayList<Trip>();
  private FragmentActivity context = null;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    // return super.onCreateView(inflater, container, savedInstanceState);
    View v = inflater.inflate(R.layout.navi_lv_frg, container, false);
    return v;
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    // TODO Auto-generated method stub
    super.onListItemClick(l, v, position, id);
    // Toast.makeText(context, "" + position, Toast.LENGTH_SHORT).show();
    Intent intent = new Intent(context, ViewTripActivity.class);
    intent.putExtra("tripId", trips.get(position).getId());
    startActivity(intent);
  }

  private void getTrips() {
    trips = new ArrayList<Trip>();
    int userId = LocalUserInfo.getUserId(context);
    String api = "GetUserTrips";
    JSONObject params = new JSONObject();
    try {
      params.put("userId", userId);
      APIRequest.get(api, params, new APICallback() {

        @Override
        public void process(String result) {
          // TODO Auto-generated method stub
          try {
            JSONArray jsonTrips = new JSONArray(result);
            for (int i = 0; i < jsonTrips.length(); ++i) {
              Trip trip = new Trip(jsonTrips.getJSONObject(i));
              trips.add(trip);
            }
          } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          refreshList();
        }
      });
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public void onAttach(Activity activity) {
    // TODO Auto-generated method stub
    super.onAttach(activity);
    context = (FragmentActivity) activity;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onActivityCreated(savedInstanceState);
    getTrips();
  }

  private void refreshList() {
    if (trips == null || trips.size() == 0)
      return;
    ArrayList<HashMap<String, Object>> listItems = new ArrayList<HashMap<String, Object>>();
    for (int i = 0; i < trips.size(); ++i) {
      HashMap<String, Object> item = new HashMap<String, Object>();
      Trip t = trips.get(i);
      long duration = t.getDuration();
      int seconds = (int) (duration / 1000);
      int minutes = seconds / 60;
      int hours = minutes / 60;
      minutes = minutes % 60;
      seconds = seconds % 60;
      item.put("distance", String.format("%-3.0fm", t.getDistance()));
      item.put("calorie", String.format("%-3.2fKCal", t.getCalorie()));
      item.put("duration",
          String.format("%02d:%02d:%02d", hours, minutes, seconds));
      item.put("steps", String.format("%-3d", t.getSteps()));
      listItems.add(item);
    }
    SimpleAdapter adapter = new SimpleAdapter(getActivity(), listItems,
        R.layout.lv_trip_item, new String[] { "duration", "distance",
            "calorie", "steps" }, new int[] { R.id.tvDurationItem,
            R.id.tvDistanceItem, R.id.tvCalorieItem, R.id.tvStepsItem });
    setListAdapter(adapter);
  }

}
