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

/**
 * @author zhangyichuan
 */
@Slf4j
@Service
public class GlowService {
    @Autowired
    private RestTemplate restTemplate;

    private static final String IP_URL = "https://sunsetbot.top/";

    private static final String[] EVENTS = { "rise_1", "set_1", "rise_2", "set_2" };

    public String getAll(String address) {
        Glow[] glows = new Glow[4];
        for (int i = 0; i < EVENTS.length; i++) {
            glows[i] = get(address, EVENTS[i]);
            log.info("glows[i]:{}", glows[i].toString());
        }
        if (!glows[0].ok()) {
            return "";
        }
        StringBuilder content = new StringBuilder(glows[0].getFormattedSummary() + "火烧云情况\n");
        for (Glow glow : glows) {
            content.append("\n").append(glow.format()).append("\n");
        }
        return content.toString();
    }

    public Glow get(String address, String event) {
        String url = String.format("%s?intend=select_city&query_city=%s&event_date=None&event=%s",
                IP_URL, address, event);
        log.info("glow url:{}", url);
        // 创建请求头
        HttpEntity<String> entity = getStringHttpEntity();

        // 使用exchange方法发送请求
        ResponseEntity<Glow> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Glow.class);
        return response.getBody();
    }

    private static HttpEntity<String> getStringHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        // 模拟Chrome浏览器
        headers.set("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");
        headers.set("Accept", "application/json,text/plain,*/*");
        headers.set("Accept-Language", "zh-CN,zh;q=0.9");
        headers.set("Accept-Encoding", "gzip, deflate, br");
        headers.set("Connection", "keep-alive");

        // 创建带请求头的请求实体
        return new HttpEntity<>(headers);
    }
}
