package com.tencent.wxcloudrun.controller.timer;

import com.tencent.wxcloudrun.client.email.EmailService;
import com.tencent.wxcloudrun.client.glow.GlowService;
import com.tencent.wxcloudrun.dao.AccessMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * @author zhangyichuan
 * @date 2025/4/30
 */
@EnableScheduling
@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class StatCityGlow {
  private final AccessMapper accessMapper;
  private final GlowService glowService;
  private final EmailService emailService;

  /** 每24小时执行一次 */
  @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
  public void stat() {
    ArrayList<String> cityList = accessMapper.getCityList();
    for (String city : cityList) {
      // 查询火烧云情况
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        log.error("sleep error", e);
      }
      String glowRes = glowService.queryGlowStrRes(city, true);
      if ("".equals(glowRes)) {
        continue;
      }
      log.info("city is {}, glowRes is beautiful {}", city, glowRes);
      emailService.sendEmail(System.getenv("EMAIL_TO"), "火烧云情况", glowRes);
    }
    log.info("statCityGlow end, now is {}", LocalDateTime.now());
  }
}
