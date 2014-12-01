package edu.usc.palhunter.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Manage the local user info
 * 
 * @author yaguang
 * 
 */
public class LocalUserInfo {

  public static final String PREF_NAME = "USER_INFO_PREF";
  public static final String PROPERTY_USER_ID = "USER_ID";
  public static final String PROPERTY_USER_NAME = "USER_NAME";

  private static SharedPreferences getPref(Context context) {
    return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
  }

  public static int getUserId(Context context) {
    return getPref(context).getInt(PROPERTY_USER_ID, 0);
  }

  public static void setUserId(Context context, int userId) {
    Editor editor = getPref(context).edit();
    editor.putInt(PROPERTY_USER_ID, userId);
    editor.commit();
  }

  public static String getUserName(Context context) {
    return getPref(context).getString(PROPERTY_USER_NAME, "User");
  }

  public static void setUserName(Context context, String userName) {
    Editor editor = getPref(context).edit();
    editor.putString(PROPERTY_USER_NAME, userName);
    editor.commit();
  }
}
