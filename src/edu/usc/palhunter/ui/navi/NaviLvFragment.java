package edu.usc.palhunter.ui.navi;


import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import edu.usc.palhunter.R;

public class NaviLvFragment extends ListFragment {
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.navi_lv_frg, container, false);
	}
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ArrayList<HashMap<String, Object>> listItems = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < 16; i++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("navi_lv_item_text", "Sample List " + i);
			listItems.add(item);
		}
		SimpleAdapter adapter = new SimpleAdapter(getActivity(),
				listItems, R.layout.navi_lv_item,
				new String[] {"navi_lv_item_text"}, new int[] {R.id.navi_lv_item_text});
		
		setListAdapter(adapter);
	}
}
