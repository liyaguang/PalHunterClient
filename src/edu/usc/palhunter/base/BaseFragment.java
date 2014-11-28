package edu.usc.palhunter.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import edu.usc.palhunter.util.Utils;

public class BaseFragment extends Fragment {
  private final String TAG = this.getClass().getSimpleName();

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    Utils.logh(TAG, " >>> onAttach");
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Utils.logh(TAG, " > onCreate");
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Utils.logh(TAG, " - onDestroy");
  }

  @Override
  public void onDetach() {
    super.onDetach();
    Utils.logh(TAG, " --- onDetach");
  }

  @Override
  public void onStart() {
    super.onStart();
    Utils.logh(TAG, " >> onStart");
  }

  @Override
  public void onStop() {
    super.onStop();
    Utils.logh(TAG, " -- onStop");
  }

}
