package com.tencent.wxcloudrun.client.GlowService;

import lombok.Data;

/**
 * @author zhangyichuan
 * @date 2025/4/30
 */
@Data
public class GlowServiceReq {
  private static final String IP_URL = "https://sunsetbot.top/";
  private final String address;
  private final String event;

  public GlowServiceReq(String address, String event) {
    this.address = address;
    this.event = event;
  }

  public String genUrl() {
    return String.format(
        "%s?intend=select_city&query_city=%s&event_date=None&event=%s", IP_URL, address, event);
  }
}
