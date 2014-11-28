package edu.usc.palhunter.ui.navi;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import edu.usc.palhunter.R;

public class NaviVpFragment extends Fragment implements
    ViewPager.OnPageChangeListener {
  private LinearLayout mImageIndex;
  private ViewPager mViewPager;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.navi_vp_frg, container, false);
    getViews(view);
    return view;
  }

  private void getViews(View view) {
    mImageIndex = (LinearLayout) view
        .findViewById(R.id.navi_vp_index_container);
    mImageIndex.removeAllViews();
    mViewPager = (ViewPager) view.findViewById(R.id.navi_vp_view_pager);
    ArrayList<View> list = new ArrayList<View>();
    for (int i = 0; i < 4; i++) {
      // add for view pager
      ImageView img = new ImageView(getActivity());
      img.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
          LayoutParams.WRAP_CONTENT));
      img.setPadding(8, 8, 8, 8);
      img.setImageResource(R.drawable.ic_launcher);
      list.add(img);
      // add for index container
      ImageView index = new ImageView(getActivity());
      index.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
          LayoutParams.WRAP_CONTENT));
      index.setPadding(8, 8, 8, 8);
      index.setImageResource(R.drawable.shelf_circle_selector);
      index.setSelected(i == 0 ? true : false);
      mImageIndex.addView(index);
    }
    mViewPager.setAdapter(new ViewPagerAdapter(list));
    mViewPager.setOnPageChangeListener(this);
  }

  @Override
  public void onPageScrollStateChanged(int arg0) {
  }

  @Override
  public void onPageScrolled(int arg0, float arg1, int arg2) {
  }

  @Override
  public void onPageSelected(int index) {
    int cnt = mImageIndex.getChildCount();
    for (int i = 0; i < cnt; i++) {
      mImageIndex.getChildAt(i).setSelected(i == index ? true : false);
    }
  }

  private class ViewPagerAdapter extends PagerAdapter {
    private ArrayList<View> mList;

    public ViewPagerAdapter(ArrayList<View> views) {
      mList = views;
    }

    @Override
    public int getCount() {
      return mList.size();
    }

    @Override
    public Object instantiateItem(View container, int position) {
      View view = mList.get(position);
      ((ViewPager) container).addView(view, 0);
      return view;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
      return (arg0 == arg1);
    }

    @Override
    public Parcelable saveState() {
      return super.saveState();
    }

    @Override
    public void startUpdate(View container) {
    }

    @Override
    public void finishUpdate(View container) {
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
      super.restoreState(state, loader);
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
      ((ViewPager) container).removeView(mList.get(position));
    }
  }
}
