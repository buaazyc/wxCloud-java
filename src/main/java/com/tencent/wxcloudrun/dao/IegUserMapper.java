package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.dataobject.IegUserDO;
import org.apache.ibatis.annotations.Mapper;

/** @author zhangyichuan */
@Mapper
public interface IegUserMapper {

    /**
     * 获取用户
     *
     * @param userName 用户名
     * @return 用户
     */
    IegUserDO getUserByName(String userName);
}
