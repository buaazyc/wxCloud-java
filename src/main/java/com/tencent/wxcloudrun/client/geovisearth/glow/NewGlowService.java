package com.tencent.wxcloudrun.client.geovisearth.glow;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tencent.wxcloudrun.entity.GlowEntity;
import com.tencent.wxcloudrun.entity.NewGlowEntity;
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
    private final Cache<String, String> cache =
            Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build();

    public NewGlowEntity queryGlow(String location) {
        String start = TimeUtils.getDay(0)+"00";
        String end = TimeUtils.getDay(3)+"23";
        String url = new NewGlowReq(location, start, end).genUrl();
        log.info("queryGlow url: {}", url);
        ResponseEntity<NewGlowRsp> glowServiceRsp =
                restTemplate.exchange(url, HttpMethod.GET, getStringHttpEntity(), NewGlowRsp.class);
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
        return entity;
    }


    private static HttpEntity<String> getStringHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        // 模拟Chrome浏览器（基于macOS）
        headers.set("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36");
        headers.set("Accept", "*/*");
        headers.set("Accept-Language", "zh-CN,zh;q=0.9");
        headers.set("Accept-Encoding", "gzip, deflate, br, zstd");
        headers.set("Sec-Ch-Ua", "\"Chromium\";v=\"136\", \"Google Chrome\";v=\"136\", \"Not.A/Brand\";v=\"99\"");
        headers.set("Sec-Ch-Ua-Mobile", "?0");
        headers.set("Sec-Ch-Ua-Platform", "\"macOS\"");
        headers.set("Sec-Fetch-Dest", "empty");
        headers.set("Sec-Fetch-Mode", "cors");
        headers.set("Sec-Fetch-Site", "same-origin");
        headers.set("X-Requested-With", "XMLHttpRequest");
        headers.set("Connection", "keep-alive");
        headers.set("Priority", "u=1, i");
        // 创建带请求头的请求实体
        return new HttpEntity<>(headers);
    }
}
