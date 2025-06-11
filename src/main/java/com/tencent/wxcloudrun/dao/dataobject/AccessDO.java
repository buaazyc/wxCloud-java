package com.tencent.wxcloudrun.dao.dataobject;

import com.tencent.wxcloudrun.service.provider.WxRequest;
import com.tencent.wxcloudrun.service.provider.WxResponse;
import java.io.Serializable;
import java.util.Map;
import lombok.Data;

/**
 * 访问记录实体类
 *
 * @author zhangyichuan
 */
@Data
public class AccessDO implements Serializable {

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

  /** 请求头 */
  private String header;

  /** 请求内容 */
  private String req;

  /** 响应内容 */
  private String rsp;

  private String accessType;

  private String accessKey;

  /** 全参数构造函数 */
  public AccessDO(
      String msgId,
      String msgType,
      String fromUserName,
      String toUserName,
      Integer createTime,
      String header,
      String req,
      String rsp,
      String accessType,
      String accessKey) {
    this.msgId = msgId;
    this.msgType = msgType;
    this.fromUserName = fromUserName;
    this.toUserName = toUserName;
    this.createTime = createTime;
    this.header = header;
    this.req = req;
    this.rsp = rsp;
    this.accessType = accessType;
    this.accessKey = accessKey;
  }

  public AccessDO(
      Map<String, String> headers,
      WxRequest req,
      WxResponse rsp,
      String access_type,
      String access_key) {
    this.msgId = req.getMsgId();
    this.msgType = req.getMsgType();
    this.fromUserName = req.getFromUserName();
    this.toUserName = req.getToUserName();
    this.createTime = req.getCreateTime();
    this.header = headers.toString();
    this.req = req.getContent();
    this.rsp = rsp.getContent();
    this.accessType = access_type;
    this.accessKey = access_key;
  }
}
