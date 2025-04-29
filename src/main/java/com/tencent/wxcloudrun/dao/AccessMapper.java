package com.tencent.wxcloudrun.dao;

import org.apache.ibatis.annotations.Mapper;

import com.tencent.wxcloudrun.model.Access;

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
  void insertAccess(Access access);
}
