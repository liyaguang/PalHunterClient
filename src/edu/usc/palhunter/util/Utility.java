package edu.usc.palhunter.util;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;

import edu.usc.palhunter.roadnetwork.IGeoPoint;

public class Utility {

  public static String streamToString(InputStream is) {
    Scanner s = new Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  }


}
