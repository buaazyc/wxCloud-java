package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.entity.NotifyHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zhangyichuan
 */
@Mapper
public interface NotifyHistoryMapper {
  /**
   * 插入通知记录
   *
   * @param notifyHistory 通知记录
   */
  Boolean insertNotifyHistory(NotifyHistory notifyHistory);
}
