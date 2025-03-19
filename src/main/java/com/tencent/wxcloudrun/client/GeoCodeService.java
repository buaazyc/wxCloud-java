package com.tencent.wxcloudrun.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.tencent.wxcloudrun.model.Geocode;

@Service
public class GeoCodeService {
    private final RestTemplate restTemplate;

    public GeoCodeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // https://restapi.amap.com/v3/geocode/geo?address=%E5%8C%97%E4%BA%AC&key=2f19b91a99aac78221146ddd5b40f5c8
    public Geocode get(String address) {
        return restTemplate.getForObject(
                "https://restapi.amap.com/v3/geocode/geo?address=" + address + "&key=2f19b91a99aac78221146ddd5b40f5c8",
                Geocode.class);
    }
}
