package com.tencent.wxcloudrun.client.solar;

/**
 * @author zhangyichuan
 * @date 2025/6/7
 */
public class SolarReq {
  private static final String URL = "https://api.open.geovisearth.com/v2/solar/info/advanced";

  /** 经纬度，用逗号隔开 */
  private final String location;

  public SolarReq(String location) {
    this.location = location;
  }

  public String genUrl() {
    return URL + "?location=" + location + "&token=" + System.getenv("GLOW_TOKEN");
  }
}
