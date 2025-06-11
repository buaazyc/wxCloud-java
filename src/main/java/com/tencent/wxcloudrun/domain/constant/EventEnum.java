package com.tencent.wxcloudrun.domain.constant;

import lombok.Getter;

/**
 * @author zhangyichuan
 * @date 2025/5/21
 */
@Getter
public class EventEnum {

  public static final EventEnum RISE_1 = new EventEnum("rise_1", "今天-日出");

  public static final EventEnum RISE_2 = new EventEnum("rise_2", "明天-日出");

  public static final EventEnum SUNSET_1 = new EventEnum("set_1", "今天-日落");

  public static final EventEnum SUNSET_2 = new EventEnum("set_2", "明天-日落");

  private final String queryLabel;

  private final String desc;

  EventEnum(String queryLabel, String desc) {
    this.queryLabel = queryLabel;
    this.desc = desc;
  }
}
