package com.tencent.wxcloudrun.model;

import java.io.Serializable;
import lombok.Data;

/**
 * @author zhangyichuan
 * @date 2025/4/30
 */
@Data
public class GlowHistory implements Serializable {

  /** 消息ID */
  private String msgId;

  /** 发送者用户名 */
  private String fromUserName;

  /** 创建时间 */
  private Integer createTime;

  /** 地址 */
  private String address;

  /** 数值质量 */
  private String numQuality;

  /** 字符串质量 */
  private String strQuality;

  public GlowHistory(
      String msgId,
      String fromUserName,
      Integer createTime,
      String address,
      String numQuality,
      String strQuality) {
    this.msgId = msgId;
    this.fromUserName = fromUserName;
    this.createTime = createTime;
    this.address = address;
    this.numQuality = numQuality;
    this.strQuality = strQuality;
  }
}
