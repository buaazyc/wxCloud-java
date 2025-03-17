package com.tencent.wxcloudrun.dto;

import lombok.Data;

@Data
public class WxRequest {
    private String ToUserName;
    private String FromUserName;
    private Integer CreateTime;
    private String MsgType;
    private String MsgId;
}
