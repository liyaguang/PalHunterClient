package edu.usc.palhunter.ui.sidebar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.internal.ex;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import edu.usc.palhunter.MapActivity;
import edu.usc.palhunter.R;
import edu.usc.palhunter.db.LocalUserInfo;
import edu.usc.palhunter.roadnetwork.IGeoPoint;
import edu.usc.palhunter.util.APIRequest;
import edu.usc.palhunter.util.APIRequest.APICallback;

public class FriendsFragment extends ListFragment {
  FragmentActivity context;
  JSONArray friends = null;

  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.navi_lv_frg, container, false);
  }

  private List<String> getFriends() {
    String api = "GetFriends";
    int userId = LocalUserInfo.getUserId(context);
    JSONObject params = new JSONObject();
    try {
      params.put("userId", userId);
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    APIRequest.get(api, params, new APICallback() {

      @Override
      public void process(String result) {
        // TODO Auto-generated method stub
        try {
          friends = new JSONArray(result);
          refreshContent();
        } catch (JSONException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }

    });
    List<String> list = new ArrayList<String>();
    String[] friends = new String[] { "Yaguang", "Luan Tran", "Ding xiong",
        "Hien To", "Joseph" };
    List<IGeoPoint> location = new ArrayList<IGeoPoint>();
    for (String friend : friends) {
      list.add(friend);
    }
    for (int i = friends.length; i < 20; ++i) {
      list.add("Friends " + i);
    }
    return list;
  }

  private void refreshContent() {

    ArrayList<HashMap<String, Object>> listItems = new ArrayList<HashMap<String, Object>>();
    for (int i = 0; i < friends.length(); ++i) {
      try {
        HashMap<String, Object> item = new HashMap<String, Object>();
        JSONObject friend = friends.getJSONObject(i);
        item.put("userName", friend.getString("nick"));
        item.put("dist", String.format("%.2fm", friend.getDouble("dist")));
        listItems.add(item);
      } catch (JSONException exc) {
        exc.printStackTrace();
      }
    }
    SimpleAdapter adapter = new SimpleAdapter(getActivity(), listItems,
        R.layout.lv_friend_item, new String[] { "userName", "dist" },
        new int[] { R.id.lv_tvUserNameItem, R.id.lv_tvFriendDistance });

    setListAdapter(adapter);
  }

  @Override
  public void onAttach(Activity activity) {
    // TODO Auto-generated method stub
    super.onAttach(activity);
    this.context = (FragmentActivity) activity;
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    // TODO Auto-generated method stub
    super.onListItemClick(l, v, position, id);
    Intent intent = new Intent(context, MapActivity.class);
    try {
      JSONObject friend = friends.getJSONObject(position);
      intent.putExtra("lat", friend.getDouble("lat"));
      intent.putExtra("lng", friend.getDouble("lng"));
      intent.putExtra("nick", friend.getString("nick"));
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    startActivity(intent);

  }

  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getFriends();
  }
}
