package com.tencent.wxcloudrun.client.glow;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tencent.wxcloudrun.domain.entity.GlowEntity;
import com.tencent.wxcloudrun.domain.utils.HttpUtils;
import com.tencent.wxcloudrun.domain.utils.TimeUtils;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author zhangyichuan
 * @date 2025/6/5
 */
@Slf4j
@Service
public class GlowService {
  @Autowired private RestTemplate restTemplate;

  /** 缓存1h */
  private final Cache<String, GlowEntity> cache =
      Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build();

  public GlowEntity queryGlow(String location) {
    long startTime = System.currentTimeMillis();
    GlowEntity rspInCache = cache.getIfPresent(location);
    if (rspInCache != null) {
      log.info(
          "queryGlow cache hit, location = {}, rspInCache = {}, cost = {}ms",
          location,
          rspInCache,
          System.currentTimeMillis() - startTime);
      return rspInCache;
    }

    String start = TimeUtils.getDay(0) + "00";
    String end = TimeUtils.getDay(3) + "23";
    String url = new GlowReq(location, start, end).genUrl();
    log.info("queryGlow cache miss, location = {} url = {}", location, url);

    ResponseEntity<GlowRsp> glowServiceRsp =
        restTemplate.exchange(url, HttpMethod.GET, HttpUtils.getStringHttpEntity(), GlowRsp.class);
    GlowRsp glowServiceRspBody = glowServiceRsp.getBody();
    if (glowServiceRspBody == null) {
      log.error("glowServiceRspBody is null");
      return new GlowEntity();
    }
    if (!glowServiceRspBody.ok()) {
      log.error("glow query error, address = {}, rsp = {}", location, glowServiceRspBody);
      return new GlowEntity();
    }
    log.info(
        "glowServiceRspBody: {}, cost = {}ms",
        glowServiceRspBody,
        System.currentTimeMillis() - startTime);
    GlowEntity entity = glowServiceRspBody.rspToEntity();
    if (entity.isNoData()) {
      log.error("GlowEntity is no data, address = {}, rsp = {}", location, entity);
      return new GlowEntity();
    }
    cache.put(location, entity);
    return entity;
  }
}
