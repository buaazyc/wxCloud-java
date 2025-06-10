package com.tencent.wxcloudrun.client.solar;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author zhangyichuan
 * @date 2025/6/7
 */
@Slf4j
@Service
public class SolarService {
  @Autowired private RestTemplate restTemplate;

  /** 缓存3天 */
  private final Cache<String, SolarRsp> cache =
      Caffeine.newBuilder().expireAfterWrite(3, TimeUnit.DAYS).build();

  public SolarRsp querySolar(String location) {
    long startTime = System.currentTimeMillis();
    SolarRsp rspInCache = cache.getIfPresent(location);
    if (rspInCache != null) {
      log.info(
          "querySolar cache hit, location = {}, rspInCache = {}, cost = {}ms",
          location,
          rspInCache,
          System.currentTimeMillis() - startTime);
      return rspInCache;
    }

    String url = new SolarReq(location).genUrl();
    log.info("querySolar cache miss, location = {} url = {}", location, url);

    ResponseEntity<SolarRsp> solarServiceRsp = restTemplate.getForEntity(url, SolarRsp.class);
    if (solarServiceRsp.getStatusCode().is2xxSuccessful()) {
      SolarRsp solarRsp = solarServiceRsp.getBody();
      if (solarRsp != null && solarRsp.getStatus() == 0) {
        cache.put(location, solarRsp);
        log.info(
            "querySolar rsp = {}, cost = {}ms", solarRsp, System.currentTimeMillis() - startTime);
        return solarRsp;
      }
    }
    return null;
  }
}
