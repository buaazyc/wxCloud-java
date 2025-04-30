package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.GlowHistory;

/**
 * @author zhangyichuan
 */
public interface GlowHistoryMapper {
  /**
   * 插入访问记录
   *
   * @param glowHistory 访问记录
   */
  void insertGlowHistory(GlowHistory glowHistory);
}
