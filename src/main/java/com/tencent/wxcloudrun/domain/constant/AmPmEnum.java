package com.tencent.wxcloudrun.domain.constant;

import lombok.Getter;

/**
 * @author zhangyichuan
 * @date 2025/6/16
 */
@Getter
public class AmPmEnum {

  public static final AmPmEnum AM = new AmPmEnum("am", "上午");

  public static final AmPmEnum PM = new AmPmEnum("pm", "下午");

  private final String name;

  private final String desc;

  public AmPmEnum(String name, String desc) {
    this.name = name;
    this.desc = desc;
  }
}
