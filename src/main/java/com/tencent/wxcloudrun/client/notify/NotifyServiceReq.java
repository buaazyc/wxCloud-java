package com.tencent.wxcloudrun.client.notify;

import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.boot.configurationprocessor.json.JSONObject;

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

  @SneakyThrows
  public JSONObject genReq() {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("touser", user);
    jsonObject.put("msgtype", "text");
    JSONObject text = new JSONObject();
    text.put("content", content);
    jsonObject.put("text", text);
    return jsonObject;
  }
}
