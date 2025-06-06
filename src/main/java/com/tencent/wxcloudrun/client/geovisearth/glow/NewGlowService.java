package com.tencent.wxcloudrun.client.geovisearth.glow;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tencent.wxcloudrun.entity.GlowEntity;
import com.tencent.wxcloudrun.entity.NewGlowEntity;
import com.tencent.wxcloudrun.time.HttpUtils;
import com.tencent.wxcloudrun.time.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangyichuan
 * @date 2025/6/5
 */

@Slf4j
@Service
public class NewGlowService {
    @Autowired
    private RestTemplate restTemplate;

    /** 缓存1h */
    private final Cache<String, NewGlowEntity> cache =
            Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build();

    public NewGlowEntity queryGlow(String location) {
        NewGlowEntity rspInCache = cache.getIfPresent(location);
        if (rspInCache != null) {
            log.info("queryGlow cache hit, location = {}, rspInCache = {}", location, rspInCache);
            return rspInCache;
        }
        log.info("queryGlow cache miss, location = {}", location);
        String start = TimeUtils.getDay(0)+"00";
        String end = TimeUtils.getDay(3)+"23";
        String url = new NewGlowReq(location, start, end).genUrl();
        log.info("queryGlow url: {}", url);
        ResponseEntity<NewGlowRsp> glowServiceRsp =
                restTemplate.exchange(url, HttpMethod.GET, HttpUtils.getStringHttpEntity(), NewGlowRsp.class);
        log.info("Status Code: {}", glowServiceRsp.getStatusCodeValue());
        NewGlowRsp glowServiceRspBody = glowServiceRsp.getBody();
        if (glowServiceRspBody == null) {
            log.error("glowServiceRspBody is null");
            return new NewGlowEntity();
        }
        if (!glowServiceRspBody.ok()) {
            log.error("glow query error, address = {}, rsp = {}", location, glowServiceRspBody);
            return new NewGlowEntity();
        }
        log.info("glowServiceRspBody: {}", glowServiceRspBody);
        NewGlowEntity entity = glowServiceRspBody.rspToEntity();
        log.info("NewGlowEntity: {}", entity);
        cache.put(location, entity);
        return entity;
    }
}
