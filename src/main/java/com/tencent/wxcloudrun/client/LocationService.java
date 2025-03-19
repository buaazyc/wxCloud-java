package com.tencent.wxcloudrun.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.tencent.wxcloudrun.model.Location;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LocationService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${AMAP_API_KEY}")
    private String apiKey;

    private static final String IP_URL = "https://restapi.amap.com/v5/ip";

    public Location get(String ip) {
        String url = String.format("%s?key=%s&ip=%s&type=4", IP_URL, apiKey, ip);
        log.info("开始请求高德地图IP定位服务，url: {}", url);
        return restTemplate.getForObject(url, Location.class);
    }
}
