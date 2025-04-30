package com.tencent.wxcloudrun.client.GlowService;

import com.tencent.wxcloudrun.model.Glow;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author zhangyichuan
 */
@Slf4j
@Service
public class GlowService {
  @Autowired private RestTemplate restTemplate;

  private static final String[] EVENTS = {"rise_1", "set_1", "rise_2", "set_2"};

  public ArrayList<Glow> queryGlowRes(String address) {
    ArrayList<Glow> glowArrayList = new ArrayList<>();
    for (String event : EVENTS) {
      String url = new GlowServiceReq(address, event).genUrl();
      // 使用exchange方法发送请求
      ResponseEntity<GlowServiceRsp> glowServiceRsp =
          restTemplate.exchange(url, HttpMethod.GET, getStringHttpEntity(), GlowServiceRsp.class);
      GlowServiceRsp glowServiceRspBody = glowServiceRsp.getBody();
      if (glowServiceRspBody == null) {
        log.error("glowServiceRspBody is null");
        return new ArrayList<>();
      }
      Glow glowRsp = glowServiceRspBody.toGlow();
      glowArrayList.add(glowRsp);
      log.info("glow: {}", glowRsp);
    }
    return glowArrayList;
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
