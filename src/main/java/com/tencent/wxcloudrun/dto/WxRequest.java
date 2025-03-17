package com.tencent.wxcloudrun.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class WxRequest {
    @JsonProperty("ToUserName")
    private String toUserName;

    @JsonProperty("FromUserName")
    private String fromUserName;

    @JsonProperty("CreateTime")
    private Integer createTime;

    @JsonProperty("MsgType")
    private String msgType;

    @JsonProperty("MsgId")
    private String msgId;
}
