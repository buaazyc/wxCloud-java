package com.tencent.wxcloudrun.client.city;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author  zhangyichuan
 * @date  2025/5/23
 */
@Slf4j
@Component
public class CityService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String CITY_QUERY_URL = "https://sunsetbot.top/?intend=change_city&city_name_incomplete=";

    public String queryCity(String address) {
        String url = CITY_QUERY_URL+address;
        ResponseEntity<CityServiceRsp> rsp = restTemplate.exchange(url, HttpMethod.GET,
                getStringHttpEntity(), CityServiceRsp.class);
        log.info("CityService queryCity address = {} response:{}", address, rsp);
        if (rsp.getBody() != null) {
            String[] cityList = rsp.getBody().getCityList();
            if (cityList != null && cityList.length > 0) {
                return cityList[0];
            }
        }
        return address;
    }

    private static HttpEntity<String> getStringHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        // 模拟Chrome浏览器
        headers.set(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");
        headers.set("Accept", "application/json,text/plain,*/*");
        headers.set("Accept-Language", "zh-CN,zh;q=0.9");
        headers.set("Accept-Encoding", "gzip, deflate, br");
        headers.set("Connection", "keep-alive");
        // 创建带请求头的请求实体
        return new HttpEntity<>(headers);
    }
}
