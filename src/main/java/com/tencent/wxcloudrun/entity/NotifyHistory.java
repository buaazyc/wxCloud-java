package com.tencent.wxcloudrun.entity;

import lombok.Data;

/**
 * @author zhangyichuan
 * @date 2025/4/30
 */
@Data
public class NotifyHistory {
  private String date;

  private String user;

  private String address;

  public NotifyHistory(String date, String user, String address) {
    this.date = date;
    this.user = user;
    this.address = address;
  }
}
