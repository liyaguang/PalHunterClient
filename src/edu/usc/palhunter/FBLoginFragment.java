package edu.usc.palhunter;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import edu.usc.palhunter.R;

public class FBLoginFragment extends Fragment {

  private static final String TAG = "MainFragement";
  private UiLifecycleHelper uiHelper;
  private TextView userInfoTextView = null;

  private Session.StatusCallback callback = new Session.StatusCallback() {

    @Override
    public void call(Session session, SessionState state, Exception exception) {
      // TODO Auto-generated method stub
      onSessionStateChange(session, state, exception);
      Log.d(TAG, "session state:" + state.toString());
    }
  };

  private void onSessionStateChange(Session session, SessionState state,
      Exception exception) {
    if (state.isOpened()) {
      userInfoTextView.setVisibility(View.VISIBLE);
      // Request user information
      Request.executeMeRequestAsync(session, new GraphUserCallback() {

        @Override
        public void onCompleted(GraphUser user, Response response) {
          // TODO Auto-generated method stub
          if (user != null) {
            userInfoTextView.setText(buildUserInfoDisplay(user));
          }
        }
      });
      Log.i(TAG, "Logged in...");
    } else if (state.isClosed()) {
      userInfoTextView.setVisibility(View.INVISIBLE);
      Log.i(TAG, "Logged out...");
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    uiHelper = new UiLifecycleHelper(getActivity(), callback);
    uiHelper.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
      @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.activity_facebook_login, container, false);
    LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
    authButton.setFragment(this);
    authButton.setReadPermissions(Arrays.asList("public_profile",
        "user_friends", "email", "user_likes", "user_status", "user_location",
        "user_birthday"));

    userInfoTextView = (TextView) view.findViewById(R.id.userInfoTextView);
    return view;
    // return super.onCreateView(inflater, container, savedInstanceState);
  }

  private String buildUserInfoDisplay(GraphUser user) {
    StringBuilder userInfo = new StringBuilder("");

    // Example: typed access (name)
    // - no special permissions required
    userInfo.append(String.format("Name: %s\n\n", user.getName()));

    // Example: typed access (birthday)
    // - requires user_birthday permission
    userInfo.append(String.format("Birthday: %s\n\n", user.getBirthday()));

    // Example: partially typed access, to location field,
    // name key (location)
    // - requires user_location permission
    userInfo.append(String.format("Location: %s\n\n", user.getLocation()
        .getProperty("name")));

    // Example: access via property name (locale)
    // - no special permissions required
    userInfo
        .append(String.format("Locale: %s\n\n", user.getProperty("locale")));

    // Example: access via key for array (languages)
    // - requires user_likes permission
    JSONArray languages = (JSONArray) user.getProperty("languages");
    if (languages.length() > 0) {
      ArrayList<String> languageNames = new ArrayList<String>();
      for (int i = 0; i < languages.length(); i++) {
        JSONObject language = languages.optJSONObject(i);
        // Add the language name to a list. Use JSON
        // methods to get access to the name field.
        languageNames.add(language.optString("name"));
      }
      userInfo.append(String.format("Languages: %s\n\n",
          languageNames.toString()));
    }

    return userInfo.toString();
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
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    // TODO Auto-generated method stub
    super.onActivityResult(requestCode, resultCode, data);
    uiHelper.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    // TODO Auto-generated method stub
    super.onSaveInstanceState(outState);
    uiHelper.onSaveInstanceState(outState);
  }

}

