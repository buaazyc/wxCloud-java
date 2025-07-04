package com.tencent.wxcloudrun.client.sun;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tencent.wxcloudrun.domain.constant.Constants;
import com.tencent.wxcloudrun.domain.constant.EventEnum;
import com.tencent.wxcloudrun.domain.constant.QueryGlowTypeEnum;
import com.tencent.wxcloudrun.domain.entity.GlowEntity;
import com.tencent.wxcloudrun.domain.entity.QueryGlowEntity;
import com.tencent.wxcloudrun.view.GenerateGlowHtml;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired private GenerateGlowHtml generateGlowHtml;

  ExecutorService executor = Executors.newFixedThreadPool(10);

  /** 缓存2h */
  private final Cache<String, ArrayList<GlowEntity>> cache =
      Caffeine.newBuilder().expireAfterWrite(120, TimeUnit.MINUTES).build();

  public String formatGlowStrRes(ArrayList<GlowEntity> glows) {
    if (glows.isEmpty()) {
      return "";
    }
    StringBuilder content = new StringBuilder(glows.get(0).getFormattedSummary() + "火烧云预测\n");
    for (GlowEntity glow : glows) {
      content.append("\n").append(glow.detailStrFormat()).append("\n-------------------");
    }
    return content.toString();
  }

  public String formatGlowStatStrRes(ArrayList<GlowEntity> glows) {
    if (glows.isEmpty()) {
      return "";
    }

    StringBuilder content = new StringBuilder(glows.get(0).getFormattedSummary() + "\n");
    for (GlowEntity glow : glows) {
      content.append("\n").append(glow.detailStrFormat()).append("\n");
    }
    return content.toString();
  }

  public String formatGlowHtmlRes(ArrayList<GlowEntity> glows) {
    return generateGlowHtml.generateHtmlContent(glows);
  }

  public ArrayList<GlowEntity> queryGlow(QueryGlowEntity queryGlowEntity) {
    ArrayList<GlowEntity> glowRes = queryGlowResWithCache(queryGlowEntity);
    if (QueryGlowTypeEnum.QUERY.equals(queryGlowEntity.getQueryType())) {
      return glowRes;
    }
    // 过滤掉不美的和过去的
    ArrayList<GlowEntity> glowResFiltered = new ArrayList<>();
    LocalDateTime now = LocalDateTime.now(ZoneId.of(Constants.LOCAL_ZONE_ID));
    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.of(Constants.LOCAL_ZONE_ID));
    for (GlowEntity glow : glowRes) {
      LocalDateTime parsedTime = LocalDateTime.parse(glow.getFormattedEventTime(), formatter);
      if (now.isBefore(parsedTime) && glow.isBeautiful()) {
        glowResFiltered.add(glow);
      }
    }
    return glowResFiltered;
  }

  public ArrayList<GlowEntity> queryGlowResWithCache(QueryGlowEntity queryGlowEntity) {
    long startTime = System.currentTimeMillis();
    ArrayList<GlowEntity> cacheGlows = cache.getIfPresent(queryGlowEntity.getCity());
    if (cacheGlows != null) {
      log.info(
          "cache hit, cost = {}ms address = {}",
          System.currentTimeMillis() - startTime,
          queryGlowEntity);

      // 异步流程更新缓存
      String traceId = MDC.get("traceid");
      executor.submit(
          () -> {
            // 如果环境变量ASYNC_UPDATE=true，则异步更新缓存
            if (!"true".equals(System.getenv("ASYNC_UPDATE"))) {
              return;
            }
            MDC.put("traceid", traceId);
            ArrayList<GlowEntity> updatedGlows = queryGlowWithThread(queryGlowEntity);
            if (updatedGlows.size() == getEvents(queryGlowEntity.getQueryType()).length) {
              cache.put(queryGlowEntity.getCity(), updatedGlows);
              log.info("Updated cache with new results");
            }
          });
      return cacheGlows;
    }

    // 未命中缓存时，同步查询结果
    ArrayList<GlowEntity> glows = queryGlowWithThread(queryGlowEntity);
    log.info(
        "cache miss, cost = {}ms address = {}",
        System.currentTimeMillis() - startTime,
        queryGlowEntity);
    // 如果glows的长度=EVENTS的长度，则缓存
    if (glows.size() == getEvents(queryGlowEntity.getQueryType()).length) {
      cache.put(queryGlowEntity.getCity(), glows);
    }
    return glows;
  }

  private ArrayList<GlowEntity> queryGlowWithThread(QueryGlowEntity queryGlowEntity) {
    List<CompletableFuture<GlowEntity>> futures = new ArrayList<>();
    String traceId = MDC.get("traceid");
    String address = queryGlowEntity.getCity();

    for (EventEnum event : getEvents(queryGlowEntity.getQueryType())) {
      CompletableFuture<GlowEntity> future =
          CompletableFuture.supplyAsync(
              () -> {
                MDC.put("traceid", traceId);
                long startTime = System.currentTimeMillis();
                String url = new GlowServiceReq(address, event.getQueryLabel()).selectCityUrl();
                log.info("queryGlow url: {}", url);
                try {
                  ResponseEntity<GlowServiceRsp> response =
                      restTemplate.getForEntity(url, GlowServiceRsp.class);
                  GlowServiceRsp glowServiceRspBody = response.getBody();
                  if (glowServiceRspBody == null) {
                    log.error("glowServiceRspBody is null");
                    return null;
                  }
                  GlowEntity glowRsp = glowServiceRspBody.toGlow();
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
    ArrayList<GlowEntity> glowArrayList = new ArrayList<>();
    for (CompletableFuture<GlowEntity> future : futures) {
      try {
        // 可能抛出 InterruptedException 或 ExecutionException
        GlowEntity result = future.get();
        if (result != null) {
          glowArrayList.add(result);
        }
      } catch (InterruptedException | ExecutionException e) {
        log.error("Error occurred while waiting for future result", e);
      }
    }
    return glowArrayList;
  }

  private EventEnum[] getEvents(QueryGlowTypeEnum queryGlowType) {
    if (queryGlowType.equals(QueryGlowTypeEnum.STAT)) {
      return new EventEnum[] {EventEnum.SUNSET_1, EventEnum.RISE_2};
    }
    return new EventEnum[] {
      EventEnum.RISE_1, EventEnum.SUNSET_1, EventEnum.RISE_2, EventEnum.SUNSET_2
    };
  }
}
