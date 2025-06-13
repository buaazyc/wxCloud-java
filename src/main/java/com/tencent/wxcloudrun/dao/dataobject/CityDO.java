package com.tencent.wxcloudrun.dao.dataobject;

import lombok.Data;

/**
 * @author zhangyichuan
 * @date 2025/6/13
 */
@Data
public class CityDO {
  private String input;

  private String city;

  public CityDO(String input, String city) {
    this.input = input;
    this.city = city;
  }
}
