package edu.usc.palhunter.ui.navi;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import edu.usc.palhunter.R;

public class BottomNavigateActivity extends FragmentActivity {

  private RadioGroup mSwitcher;
  private ViewPager mSearchVp;
  private final int CB_INDEX_HP = 0;
  private final int CB_INDEX_LV = 1;
  private final int CB_INDEX_VP = 2;

  private OnCheckedChangeListener mCheckedChgLitener = new OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
      int cur = CB_INDEX_HP;
      switch (checkedId) {
      case R.id.navi_switcher_item_hp:
        cur = CB_INDEX_HP;
        break;
      case R.id.navi_switcher_item_lv:
        cur = CB_INDEX_LV;
        break;
      case R.id.navi_switcher_item_vp:
        cur = CB_INDEX_VP;
        break;
      }
      if (mSearchVp.getCurrentItem() != cur) {
        mSearchVp.setCurrentItem(cur);
      }
    }
  };
  private OnPageChangeListener mPageChgListener = new OnPageChangeListener() {
    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
        int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
      int vpItem = mSearchVp.getCurrentItem();
      switch (vpItem) {
      case CB_INDEX_HP:
        mSwitcher.check(R.id.navi_switcher_item_hp);
        break;
      case CB_INDEX_LV:
        mSwitcher.check(R.id.navi_switcher_item_lv);
        break;
      case CB_INDEX_VP:
        mSwitcher.check(R.id.navi_switcher_item_vp);
        break;
      }
    }

  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bottom_navigate);
    getViews();
  }

  private void getViews() {
    mSwitcher = (RadioGroup) findViewById(R.id.navi_switcher);
    mSwitcher.setOnCheckedChangeListener(mCheckedChgLitener);
    mSearchVp = (ViewPager) findViewById(R.id.navi_view_pager);
    mSearchVp.setAdapter(new BtmNaviSwitchAdapter(getSupportFragmentManager()));
    mSearchVp.setOnPageChangeListener(mPageChgListener);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.bottom_navigate, menu);
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
