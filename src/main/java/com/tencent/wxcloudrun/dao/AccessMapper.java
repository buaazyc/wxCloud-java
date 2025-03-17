package com.tencent.wxcloudrun.dao;

import org.apache.ibatis.annotations.Mapper;

import com.tencent.wxcloudrun.model.Access;

@Mapper
public interface AccessMapper {
    void insertAccess(Access access);
}
