package com.tencent.wxcloudrun.domain.constant;

import lombok.Getter;

/**
 * @author zhangyichuan
 * @date 2025/6/25
 */
@Getter
public class GlowModelEnum {

  public static final GlowModelEnum EC = new GlowModelEnum("EC");

  public static final GlowModelEnum GFS = new GlowModelEnum("GFS");

  private final String name;

  public GlowModelEnum(String name) {
    this.name = name;
  }
}
