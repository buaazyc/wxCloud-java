package com.tencent.wxcloudrun.client.glow;

/**
 * @author zhangyichuan
 * @date 2025/6/5
 */
public class GlowReq {
  private static final String URL = "https://api.open.geovisearth.com/v2/grid/glow/day";

  /** 经纬度，用逗号隔开 */
  private final String location;

  /** 开始时间，格式为yyyyMMdd */
  private final String start;

  /** 结束时间，格式为yyyyMMdd */
  private final String end;

  public GlowReq(String location, String start, String end) {
    this.location = location;
    this.start = start;
    this.end = end;
  }

  public String genUrl() {
    return URL
        + "?location="
        + location
        + "&start="
        + start
        + "&end="
        + end
        + "&meteCodes=aod,glow&level=true&token="
        + System.getenv("GLOW_TOKEN");
  }
}
