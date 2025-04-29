package com.tencent.wxcloudrun.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import lombok.Data;

/**
 * @author zhangyichuan
 */
@Data
public final class ApiResponse {

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

  private ApiResponse(int code, String errorMsg, Object data) {
    this.code = code;
    this.errorMsg = errorMsg;
    this.data = data;
  }

  public static ApiResponse wxMessage(
      String toUser, String fromUser, Integer cTime, String msgType, String content) {
    ApiResponse response = new ApiResponse(200, "success", new HashMap<>());
    response.setToUserName(toUser);
    response.setFromUserName(fromUser);
    response.setCreateTime(cTime);
    response.setMsgType(msgType);
    response.setContent(content);
    return response;
  }

  public static ApiResponse ok() {
    return new ApiResponse(200, "", new HashMap<>());
  }
}
