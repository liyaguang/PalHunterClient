package edu.usc.palhunter.ui.sidebar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import edu.usc.palhunter.R;

public class LeftMenuFragment extends Fragment implements OnClickListener {
  private static final String TAG = "LeftMenuFragment";

  @Override
  public View onCreateView(LayoutInflater inflater,
      @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    View view = inflater.inflate(R.layout.frg_left_menu, container, false);
    getAndSetViews(view);
    return view;
  }

  private void getAndSetViews(View view) {
    view.findViewById(R.id.left_menu_get_source).setOnClickListener(this);
    view.findViewById(R.id.left_menu_recommend).setOnClickListener(this);
    view.findViewById(R.id.left_menu_register).setOnClickListener(this);
    view.findViewById(R.id.left_menu_mailto).setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    // TODO Auto-generated method stub
    switch (v.getId()) {
    case R.id.left_menu_get_source:
      mHandle.sendEmptyMessage(MSG_SUPPORT);
      break;
    case R.id.left_menu_mailto:
      Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
          Uri.parse("mailto:"));
      emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { getResources()
          .getString(R.string.str_auth_mail) });
      startActivity(Intent.createChooser(emailIntent, null));
      break;
    case R.id.left_menu_register:
      Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources()
          .getString(R.string.str_hompage)));
      startActivity(intent);
      break;
    }
  }

  private final static int MSG_SUPPORT = 1;
  private final static int MSG_DOWNLOAD_DIALOG = 2;
  private final static int MSG_SCORE_DIALOG = 3;
  private Handler mHandle = new Handler(new Handler.Callback() {
    @Override
    public boolean handleMessage(Message msg) {
      switch (msg.what) {
      case MSG_SUPPORT:
        // getSupportDialog();
        break;
      case MSG_DOWNLOAD_DIALOG:
        // downloadDialog();
        break;
      case MSG_SCORE_DIALOG:
        // scoreDialog();
        break;
      }
      return false;
    }
  });
}
