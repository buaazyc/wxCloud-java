package com.tencent.wxcloudrun.service;

import java.util.Optional;

import com.tencent.wxcloudrun.model.Counter;

public interface CounterService {

  Optional<Counter> getCounter(Integer id);

  void upsertCount(Counter counter);

  void clearCount(Integer id);
}
