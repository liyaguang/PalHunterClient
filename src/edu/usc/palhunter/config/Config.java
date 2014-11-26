package edu.usc.palhunter.config;

public class Config {
  static String ip = "128.125.163.134";
  static String port = "9002";
  static String serverName = "PalHunterServer";
  static String serverAddr = String.format("http://%s:%s/%s", ip, port,
      serverName);
  static String apiAddr = String.format("%s/apis", serverAddr);

  public static String getServerAddress() {
    return serverAddr;
  }

  public static String getApiBaseAddr() {
    return apiAddr;
  }
}
