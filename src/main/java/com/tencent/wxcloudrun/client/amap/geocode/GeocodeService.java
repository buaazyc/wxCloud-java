package com.tencent.wxcloudrun.client.amap.geocode;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tencent.wxcloudrun.entity.GlowEntity;
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
public class GeocodeService {
    @Autowired
    private RestTemplate restTemplate;

    /** 缓存1month */
    private final Cache<String, String> cache =
            Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.DAYS).build();

    public String queryGeocodeWithCache(String address) {
        String location = cache.getIfPresent(address);
        if (location != null) {
            log.info("cache hit, address = {}, location = {}", address, location);
            return location;
        }
        log.info("cache miss, address = {}", address);
        try {
            String url = new GeocodeReq(address).genUrl();
            log.info("queryGeocode url: {}", url);
            ResponseEntity<GeocodeRsp> rsp = restTemplate.exchange(
                    url, HttpMethod.GET, getStringHttpEntity(), GeocodeRsp.class);
            GeocodeRsp rspBody = rsp.getBody();
            if (rspBody == null) {
                log.error("rspBody is null");
                return null;
            }
            if (!rspBody.ok()) {
                log.error("queryGeocode error, address = {}, rsp = {}", address, rspBody);
                return null;
            }
            location = rspBody.getGeocodes().get(0).getLocation();
            log.info("queryGeocode {} get location: {}", address, location);
            cache.put(address, location);

        }
        catch (Exception e) {
            log.error("queryGeocode error, address = {}", address, e);
        }
        return location;
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
