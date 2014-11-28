package edu.usc.palhunter.ui.sidebar;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.anim.CustomAnimation;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import edu.usc.palhunter.R;
import edu.usc.palhunter.base.BaseFragment;
import edu.usc.palhunter.util.Utils;

public class SideBarActivity extends SlidingFragmentActivity implements
    OnClickListener {
  private static final String TAG = "MainActivity";
  private SparseArray<BaseFragment> navigateMap = new SparseArray<BaseFragment>();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // setContentView(R.layout.activity_side_bar);
    setContentViews();
  }

  private void setContentViews() {
    FragmentManager fm = getSupportFragmentManager();
    SlidingMenu sm = getSlidingMenu();
    // 背景
    sm.setBackgroundColor(Color.rgb(37, 37, 37));
    // 阴影
    sm.setShadowWidthRes(R.dimen.shadow_width);
    sm.setShadowDrawable(R.drawable.slide_menu_shadow);
    // 偏移
    DisplayMetrics metrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(metrics);
    if (metrics.widthPixels > 0) {
      // 资源配置，在不同分辨率，总会有出现别扭的机型，
      // 可以通过屏幕实际宽度，按比例配置偏移，比如：黄金比例
      sm.setBehindOffset((int) (metrics.widthPixels * 0.382));
    } else {
      // 通过资源配置偏移
      sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
    }
    // 设置侧滑栏动画
    sm.setBehindCanvasTransformer((new CustomAnimation())
        .getCustomZoomAnimation());

    sm.setFadeDegree(0.35f);
    sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
    sm.setMode(SlidingMenu.LEFT);

    // 添加导航内容
    setContentView(R.layout.slide_menu_content_frame);
    navigateMap.clear();
    mapNaviToFragment(R.id.navi_item_home, new HomeFragment()); // 首页
    mapNaviToFragment(R.id.navi_item_work_notes, new FriendsFragment()); // 工作笔记
    mapNaviToFragment(R.id.navi_item_third_libs, new TripsFragment()); // 第三方
    // 设置首页默认显示
    replaceFragment(fm, R.id.navi_item_home);

    // 设置左侧滑栏内容
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
   * 初始化map
   * 
   * @param id
   *          导航view ID
   * @param fragment
   */
  private void mapNaviToFragment(int id, BaseFragment fragment) {
    View view = findViewById(id);
    Utils.logh(TAG, "mapNaviToFragment " + id + " view: " + view);
    view.setOnClickListener(this);
    view.setSelected(false);
    navigateMap.put(id, fragment);
  }

  /**
   * 执行内容切换
   * 
   * @param fm
   * @param id
   *          导航view ID
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
    // 执行替换
    FragmentTransaction trans = fm.beginTransaction();
    if (null == fm.findFragmentByTag(tag)) {
      trans.replace(R.id.content_frame, navigateMap.get(id), tag);
      // 不存在时，添加到stack，避免切换时，先前的被清除{fm.getFragments()}
      // {存在时，不添加，避免BackStackEntry不断累加}
      Utils.logh(TAG, "null +++ add to back");
      trans.addToBackStack(tag);
    } else {
      trans.replace(R.id.content_frame, fm.findFragmentByTag(tag), tag);
    }
    trans.commit();
    Utils.logh(TAG, "replace map: " + navigateMap.get(id) + "\n"
        + "---- fm tag: " + fm.findFragmentByTag(tag));
    // 重置导航选中状态
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
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.slide_bar, menu);
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

  @Override
  public void onClick(View v) {
    // TODO Auto-generated method stub
    if (clickSwitchContent(v)) {
      return;
    }
  }

  /**
   * 点击后，切换内容
   * 
   * @param view
   *          点击view
   * @return 点击view，是否为导航view
   */
  private boolean clickSwitchContent(View view) {
    int id = view.getId();
    if (navigateMap.indexOfKey(id) < 0) {
      // 点击非导航view
      return false;
    }
    Utils.logh(TAG, "switchContent " + id + " select: " + view.isSelected()
        + " view: " + view);
    if (!view.isSelected()) {
      // 当前非选中状态：需切换到新内容
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
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }
}
