package com.tencent.wxcloudrun.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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

    private static final String[] events = { "rise_1", "set_1", "rise_2", "set_2" };

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

    public Glow[] getAll(String address) {
        Glow[] glows = new Glow[4];
        for (int i = 0; i < events.length; i++) {
            glows[i] = get(address, events[i]);
        }
        return glows;
    }

    public Glow get(String address, String event) {
        String url = String.format("%s?intend=select_city&query_city=%s&event_date=None&event=%s",
                IP_URL, address, event);
        log.info("glow url:{}", url);
        // 创建请求头
        HttpHeaders headers = new HttpHeaders();
        // 模拟Chrome浏览器
        headers.set("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");
        headers.set("Accept", "application/json,text/plain,*/*");
        headers.set("Accept-Language", "zh-CN,zh;q=0.9");
        headers.set("Accept-Encoding", "gzip, deflate, br");
        headers.set("Connection", "keep-alive");

        // 创建带请求头的请求实体
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 使用exchange方法发送请求
        ResponseEntity<Glow> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Glow.class);
        return response.getBody();
    }
}
