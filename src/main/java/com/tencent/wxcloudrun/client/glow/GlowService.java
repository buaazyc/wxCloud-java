package com.tencent.wxcloudrun.client.glow;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tencent.wxcloudrun.constant.Constants;
import com.tencent.wxcloudrun.entity.Glow;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
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

  /** 缓存60min */
  private final Cache<String, ArrayList<Glow>> cache =
      Caffeine.newBuilder().expireAfterWrite(60, TimeUnit.MINUTES).build();


  public String formatGlowStrRes(ArrayList<Glow> glows) {
    if (glows.isEmpty()) {
      return "";
    }
    StringBuilder content =
            new StringBuilder(glows.get(0).getFormattedSummary() + "火烧云情况\n");
    for (Glow glow : glows) {
      content.append("\n").append(glow.detailStrFormat()).append("\n------------------------");
    }
    return content.toString();
  }

  public ArrayList<Glow> queryGlowWithFilter(String address, boolean filter) {
    ArrayList<Glow> glowRes = queryGlowResWithCache(address);
    if (!filter) {
      return glowRes;
    }
    // 过滤掉不美的和过去的
    ArrayList<Glow> glowResFiltered = new ArrayList<>();
    LocalDateTime now = LocalDateTime.now(ZoneId.of(Constants.LOCAL_ZONE_ID));
    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of(Constants.LOCAL_ZONE_ID));
    for (Glow glow : glowRes) {
      LocalDateTime parsedTime = LocalDateTime.parse(glow.getFormattedEventTime(), formatter);
      if (now.isBefore(parsedTime) && glow.isBeautiful()) {
        glowResFiltered.add(glow);
      }
    }
    return glowResFiltered;
  }

  private ArrayList<Glow> queryGlowResWithCache(String address) {
    ArrayList<Glow> cacheGlows = cache.getIfPresent(address);
    if (cacheGlows != null) {
      log.info("cache hit, address = {}", address);
      return cacheGlows;
    }
    log.info("cache miss, address = {}", address);
    ArrayList<Glow> glows = queryGlow(address);
    cache.put(address, glows);
    return glows;
  }

  private ArrayList<Glow> queryGlow(String address) {
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
      if (!glowRsp.ok()) {
        log.error("glow query error, address = {}, event = {}, rsp = {}", address, event, glowRsp);
        continue;
      }
      if (glowRsp.isNotReady()) {
        log.info(
            "glow query not ready, address = {}, event = {}, rsp = {}", address, event, glowRsp);
        continue;
      }
      glowArrayList.add(glowRsp);
      log.info("queryGlow get glow: {}", glowRsp);
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
