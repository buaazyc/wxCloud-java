package com.tencent.wxcloudrun.client.glow;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tencent.wxcloudrun.entity.GlowEntity;
import com.tencent.wxcloudrun.time.HttpUtils;
import com.tencent.wxcloudrun.time.TimeUtils;
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
    GlowEntity rspInCache = cache.getIfPresent(location);
    if (rspInCache != null) {
      log.info("queryGlow cache hit, location = {}, rspInCache = {}", location, rspInCache);
      return rspInCache;
    }
    log.info("queryGlow cache miss, location = {}", location);
    String start = TimeUtils.getDay(0) + "00";
    String end = TimeUtils.getDay(3) + "23";
    String url = new GlowReq(location, start, end).genUrl();
    log.info("queryGlow url: {}", url);
    ResponseEntity<GlowRsp> glowServiceRsp =
        restTemplate.exchange(url, HttpMethod.GET, HttpUtils.getStringHttpEntity(), GlowRsp.class);
    log.info("Status Code: {}", glowServiceRsp.getStatusCodeValue());
    GlowRsp glowServiceRspBody = glowServiceRsp.getBody();
    if (glowServiceRspBody == null) {
      log.error("glowServiceRspBody is null");
      return new GlowEntity();
    }
    if (!glowServiceRspBody.ok()) {
      log.error("glow query error, address = {}, rsp = {}", location, glowServiceRspBody);
      return new GlowEntity();
    }
    log.info("glowServiceRspBody: {}", glowServiceRspBody);
    GlowEntity entity = glowServiceRspBody.rspToEntity();
    log.info("GlowEntity: {}", entity);
    if (entity.isNoData()) {
      log.info("GlowEntity is no data, address = {}, rsp = {}", location, entity);
      return new GlowEntity();
    }
    cache.put(location, entity);
    return entity;
  }
}
