package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.dataobject.AccessDO;
import java.util.ArrayList;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zhangyichuan
 */
@Mapper
public interface AccessMapper {
  /**
   * 插入访问记录
   *
   * @param access 访问记录
   */
  void insertAccess(AccessDO access);

  /**
   * 获取最近一段时间内所有用户的最近一次访问记录
   *
   * @return 访问记录列表
   */
  ArrayList<AccessDO> getLastAccesses();

  /**
   * 获取所有用户的城市列表
   *
   * @return 访问记录列表
   */
  ArrayList<String> getCityList();
}
