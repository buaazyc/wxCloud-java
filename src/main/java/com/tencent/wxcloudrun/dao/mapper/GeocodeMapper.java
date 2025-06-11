package com.tencent.wxcloudrun.dao.mapper;

import com.tencent.wxcloudrun.dao.dataobject.GeocodeDO;
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
