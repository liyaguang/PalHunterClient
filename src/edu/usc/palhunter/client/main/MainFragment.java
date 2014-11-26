package edu.usc.palhunter.client.main;

import java.util.Arrays;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

import edu.usc.palhunter.R;

public class MainFragment extends Fragment {

  private static final String TAG = "MainFragement";
  private UiLifecycleHelper uiHelper;

  private Session.StatusCallback callback = new Session.StatusCallback() {

    @Override
    public void call(Session session, SessionState state, Exception exception) {
      // TODO Auto-generated method stub
      onSessionStateChange(session, state, exception);
      Log.d(TAG, "session state:" + state.toString());
    }
  };

  @Override
  public void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    uiHelper = new UiLifecycleHelper(getActivity(), callback);
    uiHelper.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.activity_facebook_login, container,
        false);
    // return super.onCreateView(inflater, container, savedInstanceState);
    LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
    authButton.setFragment(this);
    authButton.setReadPermissions(Arrays.asList("public_profile",
        "user_friends", "email", "user_likes", "user_status"));
    return view;
  }

  @Override
  public void onResume() {
    // TODO Auto-generated method stub
    super.onResume();

    Session session = Session.getActiveSession();
    if (session != null && (session.isOpened() || session.isClosed())) {
      onSessionStateChange(session, session.getState(), null);
    }
    uiHelper.onResume();
  }

  @Override
  public void onPause() {
    // TODO Auto-generated method stub
    super.onPause();
    uiHelper.onPause();
  }

  @Override
  public void onDestroy() {
    // TODO Auto-generated method stub
    super.onDestroy();
    uiHelper.onDestroy();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    // TODO Auto-generated method stub
    super.onSaveInstanceState(outState);
    uiHelper.onSaveInstanceState(outState);
  }

  private void onSessionStateChange(Session session, SessionState state,
      Exception exception) {
    if (state.isOpened()) {
      Log.i(TAG, "Logged in...");
    } else {
      Log.i(TAG, "Logged out...");

    }
  }
}
