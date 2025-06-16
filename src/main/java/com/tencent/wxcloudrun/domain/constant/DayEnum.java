package com.tencent.wxcloudrun.domain.constant;

import lombok.Getter;

/**
 * @author zhangyichuan
 * @date 2025/6/16
 */
@Getter
public class DayEnum {

  public static final DayEnum UNKNOWN = new DayEnum("unknown", "位置");

  public static final DayEnum TODAY = new DayEnum("today", "今天");

  public static final DayEnum TOMORROW = new DayEnum("tomorrow", "明天");

  public static final DayEnum AFTER_TOMORROW = new DayEnum("after_tomorrow", "后天");

  private final String name;

  private final String desc;

  DayEnum(String name, String desc) {
    this.name = name;
    this.desc = desc;
  }
}
