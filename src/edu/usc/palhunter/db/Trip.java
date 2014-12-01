package edu.usc.palhunter.db;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

public class Trip {

  private int tripId;
  private int userId;
  private int startPointId;
  private int endPointId;
  private double distance;
  private double calorie;
  private Timestamp startTime;
  private long duration;
  private int steps;
  private String info;
  private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

  public int getId() {
    return this.tripId;
  }

  public int getUserId() {
    return this.userId;
  }

  public int getStartPointId() {
    return this.startPointId;
  }

  public int getEndPointId() {
    return this.endPointId;
  }

  public double getDistance() {
    return this.distance;
  }

  public double getCalorie() {
    return this.calorie;
  }

  public Timestamp getStartTime() {
    return this.startTime;
  }

  public String getInfo() {
    return this.info;
  }

  public int getSteps() {
    return this.steps;
  }

  public long getDuration() {
    return this.duration;
  }

  public Trip() {

  }

  public Trip(int id, int userId, int startPointId, int endPointId,
      double distance, long duration, double calorie, int steps,
      Timestamp startTime, String info) {
    this.tripId = id;
    this.userId = userId;
    this.startPointId = startPointId;
    this.endPointId = endPointId;
    this.distance = distance;
    this.duration = duration;
    this.calorie = calorie;
    this.startTime = startTime;
    this.info = info;
    this.steps = steps;
  }

  public JSONObject toJSONObject() {
    JSONObject obj = new JSONObject();
    try {
      obj.put("tripId", this.tripId);
      obj.put("userId", this.userId);
      obj.put("startPointId", this.startPointId);
      obj.put("endPointId", this.endPointId);
      obj.put("distance", this.distance);
      obj.put("duration", this.duration);
      obj.put("calorie", this.calorie);
      obj.put("startTime", this.startTime);
      obj.put("steps", this.steps);
      obj.put("info", this.info);
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return obj;
  }

  public Trip(JSONObject trip) {
    try {
      this.tripId = trip.getInt("tripId");
      this.userId = trip.getInt("userId");
      this.startPointId = trip.getInt("startPointId");
      this.endPointId = trip.getInt("endPointId");
      this.distance = trip.getDouble("distance");
      this.duration = trip.getLong("duration");
      this.calorie = trip.getDouble("calorie");
      this.startTime = Timestamp.valueOf(trip.getString("startTime"));
      this.steps = trip.getInt("steps");
      this.info = trip.getString("info");
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public String toString() {
    return toJSONObject().toString();
  }
}
