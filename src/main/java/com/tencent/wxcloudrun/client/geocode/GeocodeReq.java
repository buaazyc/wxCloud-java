package com.tencent.wxcloudrun.client.geocode;

import lombok.Data;

/**
 * @author zhangyichuan
 * @date 2025/6/5
 */
@Data
public class GeocodeReq {
  private static final String URL = "https://restapi.amap.com/v3/geocode/geo";

  private String address;

  public GeocodeReq(String address) {
    this.address = address;
  }

  public String genUrl() {
    return String.format("%s?address=%s&key=%s", URL, address, System.getenv("AMAP_KEY"));
  }
}
