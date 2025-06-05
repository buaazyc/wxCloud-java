package com.tencent.wxcloudrun.controller.timer;

import com.tencent.wxcloudrun.client.amap.geocode.GeocodeService;
import com.tencent.wxcloudrun.client.email.EmailService;
import com.tencent.wxcloudrun.client.geovisearth.glow.NewGlowService;
import com.tencent.wxcloudrun.client.glow.GlowService;
import com.tencent.wxcloudrun.client.qwen.AliService;
import com.tencent.wxcloudrun.dao.AccessMapper;
import com.tencent.wxcloudrun.entity.GlowEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.annotation.PostConstruct;

import com.tencent.wxcloudrun.entity.NewGlowEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * @author zhangyichuan
 * @date 2025/4/30
 */
@EnableScheduling
@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class StatCityGlowTimer {
  private final AccessMapper accessMapper;

  private final GlowService glowService;

  private final EmailService emailService;

  private final AliService aliService;

  private final GeocodeService geocodeService;

  private final NewGlowService newGlowService;

  @PostConstruct
  public void runOnceOnStartup() {
    tmp();
  }
  /**
   * 定时统计火烧云情况，并发送邮件
   */
  @Scheduled(cron = "0 12 12 * * *", zone = "Asia/Shanghai")
  public void dayCronTask() {
//    checkBeautifulGlowWithEmail();
  }

  public void checkBeautifulGlowWithEmail() {
    StringBuilder subject = new StringBuilder();
    for (String city : accessMapper.getCityList()) {
      // 查询火烧云情况
      // 保护接口，每个城市查询后休眠1秒
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        log.error("sleep error", e);
      }
      // 增加过滤
      ArrayList<GlowEntity> glows = glowService.queryGlowWithFilter(city ,true);
      String glowRes = glowService.formatGlowStrRes(glows);
      if ("".equals(glowRes)) {
        continue;
      }
      // 满足过滤条件后，则为优质火烧云，发送邮件推送到管理员
      log.info("city is {}, glowRes is beautiful {}", city, glowRes);
      subject.append(glowRes).append("-----------------------------------------").append("\n");
    }
    if (subject.length() == 0) {
      log.info("no beautiful glow");
      return;
    }
    emailService.sendEmail(System.getenv("EMAIL_TO"), "火烧云情况", subject.toString());
    log.info("statCityGlow end stat res = {}", subject);
  }

  private void refreshAllCityRes() {
    for (String city : accessMapper.getCityList()) {
      // 查询火烧云情况
      // 保护接口，每个城市查询后休眠1秒
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        log.error("sleep error", e);
      }
      ArrayList<GlowEntity> glows = glowService.queryGlowResWithCache(city);
      log.info("city = {} glows = {}", city, glowService.formatGlowStrRes(glows));
    }
  }

  private void tmp() {
    for (String city : accessMapper.getCityList()) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        log.error("sleep error", e);
      }
      city = "北京市朝阳区";
      String location = geocodeService.queryGeocodeWithCache(city);
      NewGlowEntity entity = newGlowService.queryGlow(location);
      entity.setAddress(city);
      log.info(entity.format());
      break;
    }
  }
}
