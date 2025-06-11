package com.tencent.wxcloudrun.dao.dataobject;

import java.io.Serializable;
import lombok.Data;

/**
 * @author zhangyichuan
 * @date 2025/6/10
 */
@Data
public class GeocodeDO implements Serializable {
  /** 位置：经纬度坐标 */
  private String location;

  /** 文本地址 */
  private String address;

  public GeocodeDO(String location, String address) {
    this.location = location;
    this.address = address;
  }
}
