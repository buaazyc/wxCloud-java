package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.dataobject.AccessDO;
import java.util.ArrayList;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zhangyichuan
 */
@Mapper
public interface AccessMapper {

  void insertAccess(AccessDO access);

  ArrayList<String> getCityList();

  String getAccessKey(String req);
}
