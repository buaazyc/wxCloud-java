package com.tencent.wxcloudrun.config;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

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

  public static ApiResponse wxMessage(String toUser, String fromUser, Integer ctime, String msgType, String content) {
    ApiResponse response = new ApiResponse(200, "succ", new HashMap<>());
    response.setToUserName(toUser);
    response.setFromUserName(fromUser);
    response.setCreateTime(ctime);
    response.setMsgType(msgType);
    response.setContent(content);
    return response;
  }

  public static ApiResponse ok() {
    return new ApiResponse(0, "", new HashMap<>());
  }

  public static ApiResponse ok(Object data) {
    return new ApiResponse(0, "", data);
  }

  public static ApiResponse error(String errorMsg) {
    return new ApiResponse(0, errorMsg, new HashMap<>());
  }
}
