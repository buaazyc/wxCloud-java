package com.tencent.wxcloudrun.client.sun;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tencent.wxcloudrun.domain.constant.Constants;
import com.tencent.wxcloudrun.domain.constant.EventEnum;
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
public class SunGlowService {
  @Autowired private RestTemplate restTemplate;

  private static final EventEnum[] EVENTS = {
    EventEnum.RISE_1, EventEnum.SUNSET_1, EventEnum.RISE_2, EventEnum.SUNSET_2
  };

  /** 缓存59min */
  private final Cache<String, ArrayList<SunGlowEntity>> cache =
      Caffeine.newBuilder().expireAfterWrite(59, TimeUnit.MINUTES).build();

  public String formatGlowStrRes(ArrayList<SunGlowEntity> glows) {
    if (glows.isEmpty()) {
      return "";
    }
    StringBuilder content = new StringBuilder(glows.get(0).getFormattedSummary() + "火烧云预测\n");
    for (SunGlowEntity glow : glows) {
      content.append("\n").append(glow.detailStrFormat()).append("\n------------------------");
    }
    return content.toString();
  }

  public ArrayList<SunGlowEntity> queryGlowWithFilter(String address, boolean filter) {
    ArrayList<SunGlowEntity> glowRes = queryGlowResWithCache(address);
    if (!filter) {
      return glowRes;
    }
    // 过滤掉不美的和过去的
    ArrayList<SunGlowEntity> glowResFiltered = new ArrayList<>();
    LocalDateTime now = LocalDateTime.now(ZoneId.of(Constants.LOCAL_ZONE_ID));
    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.of(Constants.LOCAL_ZONE_ID));
    for (SunGlowEntity glow : glowRes) {
      LocalDateTime parsedTime = LocalDateTime.parse(glow.getFormattedEventTime(), formatter);
      if (now.isBefore(parsedTime) && glow.isBeautiful()) {
        glowResFiltered.add(glow);
      }
    }
    return glowResFiltered;
  }

  public ArrayList<SunGlowEntity> queryGlowResWithCache(String address) {
    ArrayList<SunGlowEntity> cacheGlows = cache.getIfPresent(address);
    if (cacheGlows != null) {
      log.info("cache hit, address = {}", address);
      return cacheGlows;
    }
    log.info("cache miss, address = {}", address);
    ArrayList<SunGlowEntity> glows = queryGlow(address);
    cache.put(address, glows);
    return glows;
  }

  private ArrayList<SunGlowEntity> queryGlow(String address) {
    ArrayList<SunGlowEntity> glowArrayList = new ArrayList<>();
    for (EventEnum event : EVENTS) {
      String url = new SunGlowServiceReq(address, event.getQueryLabel()).selectCityUrl();
      // 使用exchange方法发送请求
      ResponseEntity<SunGlowServiceRsp> glowServiceRsp =
          restTemplate.exchange(
              url, HttpMethod.GET, getStringHttpEntity(), SunGlowServiceRsp.class);
      log.info("Status Code: {}", glowServiceRsp.getStatusCodeValue());
      log.info("Content-Type: {}", glowServiceRsp.getHeaders().getContentType());
      SunGlowServiceRsp glowServiceRspBody = glowServiceRsp.getBody();
      if (glowServiceRspBody == null) {
        log.error("glowServiceRspBody is null");
        return new ArrayList<>();
      }
      SunGlowEntity glowRsp = glowServiceRspBody.toGlow();
      glowRsp.setEvent(event);
      if (!glowRsp.ok()) {
        log.error(
            "glow query error, address = {}, event = {}, rsp = {}",
            address,
            event.getQueryLabel(),
            glowRsp);
        continue;
      }
      if (glowRsp.isNotReady()) {
        log.warn(
            "glow query not ready, address = {}, event = {}, rsp = {}",
            address,
            event.getQueryLabel(),
            glowRsp);
        continue;
      }
      glowArrayList.add(glowRsp);
      log.info("queryGlow get glow: {}", glowRsp);
    }
    return glowArrayList;
  }

  private static HttpEntity<String> getStringHttpEntity() {
    HttpHeaders headers = new HttpHeaders();
    // 模拟Chrome浏览器（基于macOS）
    headers.set(
        "User-Agent",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36");
    headers.set("Accept", "*/*");
    headers.set("Accept-Language", "zh-CN,zh;q=0.9");
    headers.set("Accept-Encoding", "gzip, deflate, br, zstd");
    headers.set(
        "Sec-Ch-Ua",
        "\"Chromium\";v=\"136\", \"Google Chrome\";v=\"136\", \"Not.A/Brand\";v=\"99\"");
    headers.set("Sec-Ch-Ua-Mobile", "?0");
    headers.set("Sec-Ch-Ua-Platform", "\"macOS\"");
    headers.set("Sec-Fetch-Dest", "empty");
    headers.set("Sec-Fetch-Mode", "cors");
    headers.set("Sec-Fetch-Site", "same-origin");
    headers.set("X-Requested-With", "XMLHttpRequest");
    headers.set("Connection", "keep-alive");
    headers.set("Priority", "u=1, i");
    headers.set("X-Forwarded-For", "192.168.1.1");
    headers.set("Host", "sunsetbot.top");

    // 创建带请求头的请求实体
    return new HttpEntity<>(headers);
  }
}
