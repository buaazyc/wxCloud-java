package com.tencent.wxcloudrun.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.tencent.wxcloudrun.model.Glow;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GlowService {
    @Autowired
    private RestTemplate restTemplate;

    private static final String IP_URL = "https://sunsetbot.top/";

    private static final String[] events = { "rise_1", "Set_1", "rise_2", "set_2" };

    // @Value("${DATACLOUD_API_KEY}")
    // private String apiKey;

    // public Glow get(String address) {
    // // 创建格式化器
    // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHH");
    // // 获取当前日期时间
    // LocalDateTime now = LocalDateTime.now();
    // // 获取今天0点
    // LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
    // // 获取3天后的0点
    // LocalDateTime threeDaysLater = todayStart.plusDays(3);
    // // 格式化日期时间
    // String start = todayStart.format(formatter);
    // String end = threeDaysLater.format(formatter);
    // String url =
    // "https://tiles.geovisearth.com/meteorology/v1/weather/grid/glow/day/data?location="
    // + address
    // + "&start=" + start + "&end=" + end
    // + "&meteCodes=aod,glow&level=true&token=" + apiKey;
    // return restTemplate.getForObject(url, Glow.class);
    // }

    public Glow get(String address, Integer index) {
        String url = String.format("%s?intend=select_city&query_city=%s&event_date=None&event=%s&times=None", IP_URL,
                address, events[index]);
        return restTemplate.getForObject(url, Glow.class);
    }
}
