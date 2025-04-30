package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.entity.Access;
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
  void insertAccess(Access access);
}
