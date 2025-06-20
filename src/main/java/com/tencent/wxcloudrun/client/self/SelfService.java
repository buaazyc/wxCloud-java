package com.tencent.wxcloudrun.client.self;

import com.tencent.wxcloudrun.dao.mapper.AccessMapper;
import com.tencent.wxcloudrun.service.provider.WxRequest;
import com.tencent.wxcloudrun.service.provider.WxResponse;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author zhangyichuan
 * @date 2025/6/17
 */
@Data
@Service
@Slf4j
public class SelfService {
  @Autowired private RestTemplate restTemplate;

  private final AccessMapper accessMapper;

  /** 刷新火烧云每个城市的刷新间隔：30s */
  private static final int REFRESH_GLOW_TIME = 30;

  private static final String URL =
      "https://springboot-qhor-146366-5-1348470742.sh.run.tcloudbase.com/index";

  public void keepAlive() {
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    scheduler.scheduleAtFixedRate(this::refresh, 0, REFRESH_GLOW_TIME, TimeUnit.SECONDS);
  }

  public void refresh() {
    WxRequest req = new WxRequest();
    req.setToUserName("gh_a6970656ee98");
    req.setFromUserName("oBY566s96Ou1Yn16HdbxCfh_wW5c");
    req.setCreateTime(1747829973);
    req.setMsgType("text");
    req.setMsgId("test");
    for (String city : accessMapper.getCityList()) {
      req.setContent(city);
      ResponseEntity<WxResponse> responseEntity =
          restTemplate.postForEntity(URL, req, WxResponse.class);
      WxResponse body = responseEntity.getBody();
      log.info("refresh glow get body = {}", body);

      // 保护接口，每个城市查询后休眠一段时间
      try {
        Thread.sleep(REFRESH_GLOW_TIME * 1000L);
      } catch (InterruptedException e) {
        log.error("sleep error", e);
      }
    }
  }
}
