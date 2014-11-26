package edu.usc.palhunter.client.main;

import edu.usc.palhunter.R;
import edu.usc.palhunter.R.id;
import edu.usc.palhunter.R.layout;
import edu.usc.palhunter.R.menu;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

public class FacebookLoginActivity extends FragmentActivity {

  private MainFragment mainFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState == null) {
      // Add the fragment on initial activity setup
      mainFragment = new MainFragment();
      getSupportFragmentManager().beginTransaction()
          .add(android.R.id.content, mainFragment).commit();
    } else {
      // restored state info
      mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(
          android.R.id.content);
    }
    setContentView(R.layout.activity_facebook_login);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.facebook_login, menu);
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
