package com.tencent.wxcloudrun.provider;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import lombok.Data;

/**
 * @author zhangyichuan
 */
@Data
public class WxResponse {

  private Integer code;
  private String errorMsg;
  private Object data;

  @JsonProperty("ToUserName")
  private String toUserName;

  @JsonProperty("FromUserName")
  private String fromUserName;

  @JsonProperty("CreateTime")
  private Integer createTime;

  @JsonProperty("MsgType")
  private String msgType;

  @JsonProperty("Content")
  private String content;

  private WxResponse(int code, String errorMsg, Object data) {
    this.code = code;
    this.errorMsg = errorMsg;
    this.data = data;
  }

  public static WxResponse wxMessage(
      String toUser, String fromUser, Integer cTime, String msgType, String content) {
    WxResponse response = new WxResponse(200, "success", new HashMap<>());
    response.setToUserName(toUser);
    response.setFromUserName(fromUser);
    response.setCreateTime(cTime);
    response.setMsgType(msgType);
    response.setContent(content);
    return response;
  }

  public static WxResponse ok() {
    return new WxResponse(200, "", new HashMap<>());
  }
}
