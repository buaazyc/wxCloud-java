package com.tencent.wxcloudrun.domain.constant;

import lombok.Getter;

/**
 * @author zhangyichuan
 * @date 2025/7/2
 */
@Getter
public class QueryGlowTypeEnum {

  public static final QueryGlowTypeEnum QUERY = new QueryGlowTypeEnum("query", "直接查询");

  public static final QueryGlowTypeEnum STAT = new QueryGlowTypeEnum("stat", "统计");

  private final String name;

  private final String desc;

  public QueryGlowTypeEnum(String name, String desc) {
    this.name = name;
    this.desc = desc;
  }
}
