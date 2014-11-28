package edu.usc.palhunter.ui.sidebar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import edu.usc.palhunter.DisplayMessageActivity;
import edu.usc.palhunter.FBLoginActivity;
import edu.usc.palhunter.GCMDemoActivity;
import edu.usc.palhunter.MapActivity;
import edu.usc.palhunter.R;
import edu.usc.palhunter.UserInfoActivity;
import edu.usc.palhunter.base.BaseFragment;
import edu.usc.palhunter.ui.navi.BottomNavigateActivity;

public class HomeFragment extends BaseFragment implements OnClickListener {
  public final static String EXTRA_MESSAGE = "edu.usc.palhunter.MESSAGE";

  private final static String TAG = "HomeFragement";
  EditText editMessage;

  @Override
  public View onCreateView(LayoutInflater inflater,
      @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    View v = inflater.inflate(R.layout.home_frg, container, false);
    int[] buttonIds = new int[] { R.id.login_button, R.id.gmc_button,
        R.id.send_button, R.id.btn_show_map_activity,
        R.id.btn_show_user_info_activity, R.id.bottom_nav_button,
         };
    for (int buttonId : buttonIds) {
      Button b = (Button) v.findViewById(buttonId);
      if (b != null) {
        b.setOnClickListener(this);
      }
    }
    editMessage = (EditText) v.findViewById(R.id.edit_message);
    return v;
  }

  /**
   * Called when click the button
   * 
   * @param view
   */
  public void sendMessage(View view) {
    Intent intent = new Intent(getActivity(), DisplayMessageActivity.class);
    String message = editMessage.getText().toString();
    // save message using shared preference
    SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = pref.edit();
    editor.putString(getString(R.string.main_activity_saved_message), message);
    editor.commit();
    intent.putExtra(EXTRA_MESSAGE, message);
    startActivity(intent);
  }

  public void btnOpenMapClick(View view) {
    Intent intent = new Intent(getActivity(), MapActivity.class);
    startActivity(intent);
  }

  public void btnLoginClick(View view) {
    Intent intent = new Intent(getActivity(), FBLoginActivity.class);
    startActivity(intent);
  }

  public void btnShowUserInfoClick(View view) {
    Intent intent = new Intent(getActivity(), UserInfoActivity.class);
    startActivity(intent);
  }

  public void btnGCMTestClick(View view) {
    Intent intent = new Intent(getActivity(), GCMDemoActivity.class);
    startActivity(intent);
  }

  public void btnBottomNavClick(View view) {
    Intent intent = new Intent(getActivity(), BottomNavigateActivity.class);
    startActivity(intent);
  }


  public void btnCommitClick(View view) {
    // getCurrentLocation();
  }

  @Override
  public void onClick(View v) {
    // R.id.btn_update_location, R.id.btn_show_user_location, R.id.login_button,
    // R.id.gmc_button, R.id.send_button, R.id.btn_show_map_activity,
    // R.id.btn_show_user_info_activity, R.id.btn_show_user_info,
    // R.id.bottom_nav_button
    switch (v.getId()) {
    case R.id.btn_show_map_activity:
      btnOpenMapClick(v);
      break;
    case R.id.bottom_nav_button:
      btnBottomNavClick(v);
      break;
    case R.id.gmc_button:
      btnGCMTestClick(v);
      break;
    case R.id.login_button:
      btnLoginClick(v);
      break;
    case R.id.btn_show_user_info_activity:
      btnShowUserInfoClick(v);
      break;
    }
  }

}
