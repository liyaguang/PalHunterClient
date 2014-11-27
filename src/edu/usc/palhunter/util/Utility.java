package edu.usc.palhunter.util;

import java.io.InputStream;
import java.util.Scanner;

public class Utility {

  public static String streamToString(InputStream is) {
    Scanner s = new Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  }
}
