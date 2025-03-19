package com.tencent.wxcloudrun.client;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.tencent.wxcloudrun.model.Glow;

@Service
public class GlowService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${DATACLOUD_API_KEY}")
    private String apiKey;

    public Glow get(String address) {
        // 创建格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHH");
        // 获取当前日期时间
        LocalDateTime now = LocalDateTime.now();
        // 获取今天0点
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        // 获取3天后的0点
        LocalDateTime threeDaysLater = todayStart.plusDays(3);
        // 格式化日期时间
        String start = todayStart.format(formatter);
        String end = threeDaysLater.format(formatter);
        String url = "https://tiles.geovisearth.com/meteorology/v1/weather/grid/glow/day/data?location=" + address
                + "&start=" + start + "&end=" + end
                + "&meteCodes=aod,glow&level=true&token=" + apiKey;
        return restTemplate.getForObject(url, Glow.class);
    }
}
