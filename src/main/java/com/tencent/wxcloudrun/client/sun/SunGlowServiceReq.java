package com.tencent.wxcloudrun.client.sun;

import lombok.Data;

/**
 * @author zhangyichuan
 * @date 2025/4/30
 */
@Data
public class SunGlowServiceReq {
  private static final String URL = "http://sunsetbot.top/";
  private final String address;
  private final String event;

  public SunGlowServiceReq(String address, String event) {
    this.address = address;
    this.event = event;
  }

  public String selectCityUrl() {
    return String.format(
        "%s?intend=select_city&query_city=%s&event_date=None&event=%s", URL, address, event);
  }
}
