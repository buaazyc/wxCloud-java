package com.tencent.wxcloudrun.client.notify;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 * @author zhangyichuan
 * @date 2025/4/30
 */
@Data
public class NotifyServiceReq {
  private String user;
  private String content;

  public NotifyServiceReq(String user, String content) {
    this.user = user;
    this.content = content;
  }

  public Map<String, Object> genReq() {
    Map<String, Object> body = new HashMap<>(5);
    body.put("touser", user);
    body.put("msgtype", "text");
    Map<String, Object> text = new HashMap<>(5);
    text.put("content", content);
    body.put("text", text);
    return body;
  }
}
