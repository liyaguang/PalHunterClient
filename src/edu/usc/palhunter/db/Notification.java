package edu.usc.palhunter.db;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Notification {
  private int id;
  private String content;
  private int userId;
  private Timestamp time;
  private int type;

  public static final int SYSTEM_MESSAGE = 1;
  public static final int FRIEND_MESSAGE = 2;

  public Notification(int id, int userId, String content, Timestamp time,
      int type) {
    this.id = id;
    this.content = content;
    this.userId = userId;
    this.time = time;
    this.type = type;
  }

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getUserId() {
    return this.userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public String getContent() {
    return this.content;

  }

  public void setContent(String content) {
    this.content = content;
  }

  public Timestamp getTime() {
    return this.time;

  }

  public void setTime(Timestamp time) {
    this.time = time;
  }

  public int getType() {
    return this.type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public JSONObject toJSONObject() {
    JSONObject obj = new JSONObject();
    try {
      obj.put("notificationId", this.id);
      obj.put("usrId", this.userId);
      obj.put("content", this.content);
      obj.put("time", this.time);
      obj.put("type", this.type);
    } catch (JSONException exc) {
      exc.printStackTrace();
    }
    return obj;
  }

  public String toString() {
    return toJSONObject().toString();
  }
  
}