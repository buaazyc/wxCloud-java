package com.tencent.wxcloudrun.client.notify;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author zhangyichuan
 * @date 2025/4/30
 */
public class NotifyServiceRsp {
  @JsonProperty("errcode")
  private Integer errcode;

  @JsonProperty("errmsg")
  private String errmsg;

  public boolean ok() {
    return errcode == 0;
  }
}
