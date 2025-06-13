package com.tencent.wxcloudrun.client.sun;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tencent.wxcloudrun.domain.constant.Constants;
import com.tencent.wxcloudrun.domain.constant.EventEnum;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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

  ExecutorService executor = Executors.newFixedThreadPool(10);

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
    long startTime = System.currentTimeMillis();
    ArrayList<SunGlowEntity> cacheGlows = cache.getIfPresent(address);
    if (cacheGlows != null) {
      log.info(
          "cache hit, cost = {}ms address = {}", System.currentTimeMillis() - startTime, address);
      return cacheGlows;
    }

    ArrayList<SunGlowEntity> glows = queryGlowWithThread(address);
    log.info(
        "cache miss, cost = {}ms address = {}", System.currentTimeMillis() - startTime, address);
    // 如果glows的长度=EVENTS的长度，则缓存
    if (glows.size() == EVENTS.length) {
      cache.put(address, glows);
    }
    return glows;
  }

  private ArrayList<SunGlowEntity> queryGlowWithThread(String address) {
    List<CompletableFuture<SunGlowEntity>> futures = new ArrayList<>();
    String traceId = MDC.get("traceid");

    for (EventEnum event : EVENTS) {
      CompletableFuture<SunGlowEntity> future =
          CompletableFuture.supplyAsync(
              () -> {
                MDC.put("traceid", traceId);
                long startTime = System.currentTimeMillis();
                String url = new SunGlowServiceReq(address, event.getQueryLabel()).selectCityUrl();
                try {
                  ResponseEntity<SunGlowServiceRsp> glowServiceRsp =
                      restTemplate.exchange(
                          url, HttpMethod.GET, getStringHttpEntity(), SunGlowServiceRsp.class);
                  SunGlowServiceRsp glowServiceRspBody = glowServiceRsp.getBody();
                  if (glowServiceRspBody == null) {
                    log.error("glowServiceRspBody is null");
                    return null;
                  }

                  SunGlowEntity glowRsp = glowServiceRspBody.toGlow();
                  glowRsp.setEvent(event);

                  if (!glowRsp.ok()) {
                    log.error(
                        "glow query error, address = {}, event = {}, rsp = {}",
                        address,
                        event.getQueryLabel(),
                        glowRsp);
                    return null;
                  }

                  if (glowRsp.isNotReady()) {
                    log.warn(
                        "glow query not ready, address = {}, event = {}, rsp = {}",
                        address,
                        event.getQueryLabel(),
                        glowRsp);
                    return null;
                  }

                  log.info(
                      "queryGlow cost = {}ms get glow: {}",
                      System.currentTimeMillis() - startTime,
                      glowRsp);
                  return glowRsp;
                } catch (Exception e) {
                  log.error(
                      "glow query error, address = {}, event = {}",
                      address,
                      event.getQueryLabel(),
                      e);
                  return null;
                }
              },
              // 使用自定义线程池
              executor);
      futures.add(future);
    }

    // 等待所有异步任务完成，并收集结果
    ArrayList<SunGlowEntity> glowArrayList = new ArrayList<>();
    for (CompletableFuture<SunGlowEntity> future : futures) {
      try {
        // 可能抛出 InterruptedException 或 ExecutionException
        SunGlowEntity result = future.get();
        if (result != null) {
          glowArrayList.add(result);
        }
      } catch (InterruptedException | ExecutionException e) {
        log.error("Error occurred while waiting for future result", e);
      }
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
