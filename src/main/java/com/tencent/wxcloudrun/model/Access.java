package com.tencent.wxcloudrun.model;

import java.io.Serializable;

import lombok.Data;

/**
 * 访问记录实体类
 */
@Data
public class Access implements Serializable {

    /** 消息ID */
    private String msgId;

    /** 消息类型 */
    private String msgType;

    /** 发送者用户名 */
    private String fromUserName;

    /** 接收者用户名 */
    private String toUserName;

    /** 创建时间 */
    private Integer createTime;

    /** 请求内容 */
    private String req;

    /** 响应内容 */
    private String rsp;
}
