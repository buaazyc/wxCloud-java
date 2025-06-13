package com.tencent.wxcloudrun.dao.mapper;

import com.tencent.wxcloudrun.dao.dataobject.CityDO;
import java.util.ArrayList;

/**
 * @author zhangyichuan
 */
public interface CityMapper {
  void insertCity(CityDO city);

  String getCity(String input);

  ArrayList<String> getCityList();
}
