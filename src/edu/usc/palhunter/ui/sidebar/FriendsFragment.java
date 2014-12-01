package edu.usc.palhunter.ui.sidebar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import edu.usc.palhunter.R;
import edu.usc.palhunter.roadnetwork.IGeoPoint;

public class FriendsFragment extends ListFragment {
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.navi_lv_frg, container, false);
  }

  private List<String> getFriends() {
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

  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    ArrayList<HashMap<String, Object>> listItems = new ArrayList<HashMap<String, Object>>();
    List<String> friends = getFriends();
    for (int i = 0; i < friends.size(); ++i) {
      HashMap<String, Object> item = new HashMap<String, Object>();
      item.put("userName", friends.get(i));
      listItems.add(item);
    }
    SimpleAdapter adapter = new SimpleAdapter(getActivity(), listItems,
        R.layout.lv_friend_item, new String[] { "userName" },
        new int[] { R.id.lv_tvUserNameItem });

    setListAdapter(adapter);
  }
}
