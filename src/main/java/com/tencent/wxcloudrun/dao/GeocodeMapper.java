package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.dataobject.GeocodeDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zhangyichuan
 * @date 2025/6/10
 */
@Mapper
public interface GeocodeMapper {
  void insertGeocode(GeocodeDO geocode);

  String getGeocode(String address);
}
