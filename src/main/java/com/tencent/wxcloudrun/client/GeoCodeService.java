package com.tencent.wxcloudrun.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.tencent.wxcloudrun.model.Geocode;

@Service
public class GeoCodeService {
    @Autowired
    private RestTemplate restTemplate;

    // 从环境变量中读取高德地图API密钥
    @Value("${AMAP_API_KEY}")
    private String apiKey;

    public Geocode get(String address) {
        return restTemplate.getForObject(
                "https://restapi.amap.com/v3/geocode/geo?address=" + address + "&key=" + apiKey,
                Geocode.class);
    }
}
