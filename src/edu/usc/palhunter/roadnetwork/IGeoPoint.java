package edu.usc.palhunter.roadnetwork;

import java.io.Serializable;

import org.json.JSONObject;

/**
 * Point that consists of longititude and latitude
 * 
 * @author yaguang
 * 
 */
public interface IGeoPoint extends Serializable {

  public double getLat();

  public double getLng();

  public String toString();

  public JSONObject toJSONObject();
}
