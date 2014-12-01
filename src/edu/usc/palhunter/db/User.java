package edu.usc.palhunter.db;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
  private int userId;
  private String email;
  private String nick;
  private String password;
  private String avatar;
  private String facebookId;
  private int gender;

  public User(int userId, String email, String nick, int gender, String avatar,
      String facebookId) {
    this.userId = userId;
    this.email = email;
    this.nick = nick;
    this.gender = gender;
    this.facebookId = facebookId;
    this.avatar = avatar;
  }

  public User() {

  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getNick() {
    return this.nick;
  }

  public void setNick(String nick) {
    this.nick = nick;
  }

  public String getAvatar() {
    return this.avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public int getGender() {
    return this.gender;
  }

  public void setGender(int gender) {
    this.gender = gender;
  }

  public String getFacebookId() {
    return this.facebookId;
  }

  public void setFacebookId(String facebookId) {
    this.facebookId = facebookId;
  }

  public JSONObject toJSONObject() {
    JSONObject obj = new JSONObject();
    try {
      obj.put("userId", this.userId);
      obj.put("email", this.email);
      obj.put("nick", nick);
      obj.put("gender", this.gender);
      obj.put("avatar", this.avatar);
      obj.put("facebookId", this.facebookId);
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return obj;
  }

  public String toString() {
    // return String.format("Id:%d, Email:%s", getUserId(), getEmail());
    return toJSONObject().toString();
  }

}
