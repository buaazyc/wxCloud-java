package com.tencent.wxcloudrun.client.notify;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author zhangyichuan
 * @date 2025/4/30
 */
@Data
public class NotifyServiceReq {
  @JsonProperty("touser")
  private String user;
  @JsonProperty("msgtype")
  private String msgtype;
  @JsonProperty("text")
  private Text text;
  @Data
  public static class Text {
    @JsonProperty("content")
    private String content;
  }

  public NotifyServiceReq(String user, String content) {
    this.user = user;
    this.msgtype = "text";
    this.text = new Text();
    this.text.setContent(content);
  }
}
