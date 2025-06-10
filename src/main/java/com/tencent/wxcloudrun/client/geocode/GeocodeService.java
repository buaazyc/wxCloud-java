package com.tencent.wxcloudrun.client.geocode;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tencent.wxcloudrun.utils.HttpUtils;
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
public class GeocodeService {
  @Autowired private RestTemplate restTemplate;

  /** 缓存1month */
  private final Cache<String, GeocodeRsp> cache =
      Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.DAYS).build();

  public GeocodeRsp queryGeocode(String address) {
    long startTime = System.currentTimeMillis();
    GeocodeRsp rspInCache = cache.getIfPresent(address);
    if (rspInCache != null) {
      log.info(
          "queryGeocode cache hit, address = {}, rspInCache = {}, cost = {}ms",
          address,
          rspInCache,
          System.currentTimeMillis() - startTime);
      return rspInCache;
    }
    String url = new GeocodeReq(address).genUrl();
    log.info("queryGeocode cache miss, address = {}, url = {}", address, url);
    try {
      ResponseEntity<GeocodeRsp> rsp =
          restTemplate.exchange(
              url, HttpMethod.GET, HttpUtils.getStringHttpEntity(), GeocodeRsp.class);
      GeocodeRsp rspBody = rsp.getBody();
      if (rspBody == null) {
        log.error("rspBody is null");
        return null;
      }
      if (!rspBody.ok()) {
        log.error("queryGeocode error, address = {}, rsp = {}", address, rspBody);
        return null;
      }
      log.info(
          "queryGeocode address = {}, rspBody = {}, cost = {}ms",
          address,
          rspBody,
          System.currentTimeMillis() - startTime);
      cache.put(address, rspBody);
      return rspBody;

    } catch (Exception e) {
      log.error("queryGeocode error, address = {}", address, e);
    }
    return null;
  }
}
