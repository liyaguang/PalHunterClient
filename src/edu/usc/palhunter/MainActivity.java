package edu.usc.palhunter;

import android.content.Context;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.facebook.AppEventsLogger;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.anim.CustomAnimation;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import edu.usc.palhunter.data.LocalUserInfo;
import edu.usc.palhunter.ui.sidebar.FriendsFragment;
import edu.usc.palhunter.ui.sidebar.HomeFragment;
import edu.usc.palhunter.ui.sidebar.LeftMenuFragment;
import edu.usc.palhunter.ui.sidebar.TripsFragment;
import edu.usc.palhunter.util.Utils;

public class MainActivity extends SlidingFragmentActivity implements
    OnClickListener {

  private static final String TAG = "MainActivity";
  public static final String EXTRA_MESSAGE = MainActivity.class.getName();
  private SparseArray<Fragment> navigateMap = new SparseArray<Fragment>();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    init();
    setContentViews();
  }

  private void init() {
    setUserId();
  }

  private void setUserId() {
    // FIXME
    LocalUserInfo.setUserId(this, 1);
  }

  private void setContentViews() {
    FragmentManager fm = getSupportFragmentManager();
    SlidingMenu sm = getSlidingMenu();
    // Background
    sm.setBackgroundColor(Color.rgb(37, 37, 37));
    // Shadow
    sm.setShadowWidthRes(R.dimen.shadow_width);
    sm.setShadowDrawable(R.drawable.slide_menu_shadow);
    // Offset
    DisplayMetrics metrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(metrics);
    if (metrics.widthPixels > 0) {
      // set offset with certain ratio
      sm.setBehindOffset((int) (metrics.widthPixels * 0.382));
    } else {
      // set offset according to resource file
      sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
    }
    // set the sliding animation
    sm.setBehindCanvasTransformer((new CustomAnimation())
        .getCustomZoomAnimation());

    sm.setFadeDegree(0.35f);
    sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
    sm.setMode(SlidingMenu.LEFT);

    // Add navigation content
    setContentView(R.layout.slide_menu_content_frame);
    navigateMap.clear();
    mapNaviToFragment(R.id.navi_item_home, new HomeFragment()); // Home
    mapNaviToFragment(R.id.navi_item_friends, new FriendsFragment()); // Friends
    mapNaviToFragment(R.id.navi_item_trips, new TripsFragment()); // Trips
    // Set the default view
    replaceFragment(fm, R.id.navi_item_home);

    // Set the content of left slide menu
    LeftMenuFragment lmf = new LeftMenuFragment();
    setBehindContentView(R.layout.slide_menu_frame);
    fm.beginTransaction().replace(R.id.menu_frame, lmf).commit();
    Utils
        .logh(TAG,
            "replaceFragment EntryCount: "
                + fm.getBackStackEntryCount()
                + " size: "
                + (null == fm.getFragments() ? "0[null]" : fm.getFragments()
                    .size()));
  }

  /**
   * init Fragement id map
   * 
   * @param id
   *          Page view ID
   * @param fragment
   */
  private void mapNaviToFragment(int id, Fragment fragment) {
    View view = findViewById(id);
    Utils.logh(TAG, "mapNaviToFragment " + id + " view: " + view);
    view.setOnClickListener(this);
    view.setSelected(false);
    navigateMap.put(id, fragment);
  }

  /**
   * Replace the content of fragments 
   * 
   * @param fm
   * @param id
   *          view ID
   */
  private void replaceFragment(FragmentManager fm, int id) {
    Utils
        .logh(TAG,
            "replaceFragment EntryCount: "
                + fm.getBackStackEntryCount()
                + " size: "
                + (null == fm.getFragments() ? "0[null]" : fm.getFragments()
                    .size()));
    String tag = String.valueOf(id);
    // Do replacement
    FragmentTransaction trans = fm.beginTransaction();
    if (null == fm.findFragmentByTag(tag)) {
      trans.replace(R.id.content_frame, navigateMap.get(id), tag);
      // if not exist, add to stack{fm.getFragments()}
      // if exist, do not add to avoid BackStackEntry increase
      Utils.logh(TAG, "null +++ add to back");
      trans.addToBackStack(tag);
    } else {
      trans.replace(R.id.content_frame, fm.findFragmentByTag(tag), tag);
    }
    trans.commit();
    Utils.logh(TAG, "replace map: " + navigateMap.get(id) + "\n"
        + "---- fm tag: " + fm.findFragmentByTag(tag));
    // Reset focus of navigation bar
    for (int i = 0, size = navigateMap.size(); i < size; i++) {
      int curId = navigateMap.keyAt(i);
      Utils.logh(TAG, "curId: " + curId);
      if (curId == id) {
        findViewById(id).setSelected(true);
      } else {
        findViewById(curId).setSelected(false);
      }
    }
  }

  @Override
  public void onClick(View v) {
    // TODO Auto-generated method stub
    if (clickSwitchContent(v)) {
      return;
    }
  }

  /**
   * Switch content after click
   * 
   * @param view
   * @return if the view if current view 
   */
  private boolean clickSwitchContent(View view) {
    int id = view.getId();
    if (navigateMap.indexOfKey(id) < 0) {
      // not the navigation view, do not have to switch
      return false;
    }
    Utils.logh(TAG, "switchContent " + id + " select: " + view.isSelected()
        + " view: " + view);
    if (!view.isSelected()) {
      // Not the current fragment, need to switch
      replaceFragment(getSupportFragmentManager(), id);
    } else {
      Utils.logh(TAG, " ignore --- selected !!! ");
    }
    return true;

  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (KeyEvent.KEYCODE_BACK == keyCode) {
      // @see SlidingFragmentActivity
      if (getSlidingMenu().isMenuShowing()) {
        getSlidingMenu().showContent();
        return true;
      }
      // return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  private boolean checkGPS() {
    // Check GPS
    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    boolean gpsEnabled = locationManager
        .isProviderEnabled(LocationManager.GPS_PROVIDER);
    return gpsEnabled;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    MenuInflater inflater = getMenuInflater();
    // inflater.inflate(R.menu.main, menu);
    inflater.inflate(R.menu.main_activity_actions, menu);
    return super.onCreateOptionsMenu(menu);
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
  protected void onPause() {
    super.onPause();

    // Logs 'app deactivate' App Event
    AppEventsLogger.deactivateApp(this);
    Log.d(TAG, "App paused");
  }

  @Override
  protected void onStop() {
    super.onStop();
    Log.d(TAG, "App stoped");
  }

  @Override
  protected void onResume() {
    super.onResume();
    // Logs 'install' and 'app activate' App Events
    AppEventsLogger.activateApp(this);
    Log.d(TAG, "App resumed");
  }

  @Override
  protected void onStart() {
    super.onStart();
    Log.d(TAG, "App Started");
    boolean gpsEnabled = checkGPS();
    Log.d(TAG, "GPS Enabled: " + gpsEnabled);
  }

}
