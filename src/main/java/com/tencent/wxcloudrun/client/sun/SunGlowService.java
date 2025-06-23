package com.tencent.wxcloudrun.client.sun;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tencent.wxcloudrun.domain.constant.Constants;
import com.tencent.wxcloudrun.domain.constant.EventEnum;
import com.tencent.wxcloudrun.domain.utils.HttpUtils;
import com.tencent.wxcloudrun.domain.utils.TimeUtils;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
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

  /** 缓存61min，更新时间是60min，这里保证特定列表的城市可以永远可以命中缓存 */
  private final Cache<String, ArrayList<SunGlowEntity>> cache =
      Caffeine.newBuilder().expireAfterWrite(61, TimeUnit.MINUTES).build();

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

      //      String traceId = MDC.get("traceid");
      // 异步流程更新缓存
      //      executor.submit(
      //          () -> {
      //            MDC.put("traceid", traceId);
      //            ArrayList<SunGlowEntity> updatedGlows = queryGlowWithThread(address);
      //            if (updatedGlows.size() == getEvents().length) {
      //              cache.put(address, updatedGlows); // 更新缓存
      //              log.info("Updated cache with new results.");
      //            }
      //          });
      return cacheGlows;
    }

    // 未命中缓存时，同步查询结果
    ArrayList<SunGlowEntity> glows = queryGlowWithThread(address);
    log.info(
        "cache miss, cost = {}ms address = {}", System.currentTimeMillis() - startTime, address);
    // 如果glows的长度=EVENTS的长度，则缓存
    if (glows.size() == getEvents().length) {
      cache.put(address, glows);
    }
    return glows;
  }

  private ArrayList<SunGlowEntity> queryGlow(String address) {
    ArrayList<SunGlowEntity> glowArrayList = new ArrayList<>();
    for (EventEnum event : EVENTS) {
      long startTime = System.currentTimeMillis();
      String url = new SunGlowServiceReq(address, event.getQueryLabel()).selectCityUrl();
      // 使用exchange方法发送请求
      ResponseEntity<SunGlowServiceRsp> glowServiceRsp =
          restTemplate.exchange(
              url, HttpMethod.GET, HttpUtils.getStringHttpEntity(), SunGlowServiceRsp.class);
      SunGlowServiceRsp glowServiceRspBody = glowServiceRsp.getBody();
      if (glowServiceRspBody == null) {
        log.error("queryGlow glowServiceRspBody is null");
        return new ArrayList<>();
      }
      SunGlowEntity glowRsp = glowServiceRspBody.toGlow();
      glowRsp.setEvent(event);
      if (!glowRsp.ok()) {
        log.error(
            "queryGlow glow query error, address = {}, event = {}, rsp = {}",
            address,
            event.getQueryLabel(),
            glowRsp);
        continue;
      }
      if (glowRsp.isNotReady()) {
        log.warn(
            "queryGlow glow query not ready, address = {}, event = {}, rsp = {}",
            address,
            event.getQueryLabel(),
            glowRsp);
        continue;
      }
      glowArrayList.add(glowRsp);
      log.info("queryGlow cost = {}ms glow: {}", System.currentTimeMillis() - startTime, glowRsp);
    }
    return glowArrayList;
  }

  private ArrayList<SunGlowEntity> queryGlowWithThread(String address) {
    List<CompletableFuture<SunGlowEntity>> futures = new ArrayList<>();
    String traceId = MDC.get("traceid");

    for (EventEnum event : getEvents()) {
      CompletableFuture<SunGlowEntity> future =
          CompletableFuture.supplyAsync(
              () -> {
                MDC.put("traceid", traceId);
                long startTime = System.currentTimeMillis();
                String url = new SunGlowServiceReq(address, event.getQueryLabel()).selectCityUrl();
                try {
                  ResponseEntity<SunGlowServiceRsp> glowServiceRsp =
                      restTemplate.exchange(
                          url,
                          HttpMethod.GET,
                          HttpUtils.getStringHttpEntity(),
                          SunGlowServiceRsp.class);
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

  private EventEnum[] getEvents() {
    int hour = TimeUtils.today().getHour();
    // 如果当前时间在10点之前，则只查询今天
    if (hour < 10) {
      return new EventEnum[] {EventEnum.RISE_1, EventEnum.SUNSET_1};
    }
    // 如果在10点-20点之间，查询今天傍晚和明天早上
    if (hour < 20) {
      return new EventEnum[] {EventEnum.SUNSET_1, EventEnum.RISE_2};
    }
    // 如果当前时间在20点之后，则只查询明天
    return new EventEnum[] {EventEnum.RISE_2, EventEnum.SUNSET_2};
  }
}
