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

    // https://restapi.amap.com/v3/geocode/geo?address=%E5%8C%97%E4%BA%AC&key=2f19b91a99aac78221146ddd5b40f5c8
    public Geocode get(String address) {
        // todo: 这里的key要改成放在配置文件中
        return restTemplate.getForObject(
                "https://restapi.amap.com/v3/geocode/geo?address=" + address + "&key=" + apiKey,
                Geocode.class);
    }
}
