package com.tencent.wxcloudrun.domain.entity;

import com.tencent.wxcloudrun.domain.constant.QueryGlowTypeEnum;
import lombok.Data;

/**
 * @author zhangyichuan
 * @date 2025/7/2
 */
@Data
public class QueryGlowEntity {
  private String city;
  private QueryGlowTypeEnum queryType;

  public QueryGlowEntity(String city, QueryGlowTypeEnum queryType) {
    this.city = city;
    this.queryType = queryType;
  }
}
