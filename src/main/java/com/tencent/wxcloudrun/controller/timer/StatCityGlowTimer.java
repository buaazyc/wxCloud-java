package com.tencent.wxcloudrun.controller.timer;

import com.tencent.wxcloudrun.client.email.EmailService;
import com.tencent.wxcloudrun.client.geocode.GeocodeRsp;
import com.tencent.wxcloudrun.client.geocode.GeocodeService;
import com.tencent.wxcloudrun.client.glow.GlowService;
import com.tencent.wxcloudrun.client.qwen.AliService;
import com.tencent.wxcloudrun.dao.AccessMapper;
import com.tencent.wxcloudrun.dataobject.AccessDO;
import com.tencent.wxcloudrun.entity.GlowEntity;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

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

  private final EmailService emailService;

  private final AliService aliService;

  private final GeocodeService geocodeService;

  private final GlowService glowService;

  @PostConstruct
  public void runOnceOnStartup() {
    //    checkBeautifulGlowWithEmail();
  }

  /** 定时统计火烧云情况，并发送邮件 */
  @Scheduled(cron = "0 12 12 * * *", zone = "Asia/Shanghai")
  public void dayCronTask() {
    checkBeautifulGlowWithEmail();
  }

  public void checkBeautifulGlowWithEmail() {
    StringBuilder subject = new StringBuilder();
    for (String city : accessMapper.getCityList()) {
      // 查询火烧云情况
      // 保护接口，每个城市查询后休眠2秒
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        log.error("sleep error", e);
      }

      GeocodeRsp geocodeRsp = geocodeService.queryGeocode(city);
      GlowEntity glow = glowService.queryGlow(geocodeRsp.getLocation());
      glow.setAddress(geocodeRsp.getFormattedAddress());
      String glowRes = glow.emailFormatWithFilter();

      if ("".equals(glowRes)) {
        continue;
      }
      // 满足过滤条件后，则为优质火烧云，发送邮件推送到管理员
      log.info("city is {}, glowRes is beautiful {}", city, glowRes);
      subject.append(glowRes).append("---------------").append("\n");
    }
    if (subject.length() == 0) {
      log.info("no beautiful glow");
      return;
    }
    emailService.sendEmail(System.getenv("EMAIL_TO"), "火烧云情况", subject.toString());
    log.info("statCityGlow end stat res = {}", subject);
  }

  public void checkNewLlm() {
    for (AccessDO accessDO : accessMapper.getLastAccesses()) {
      String newCity = aliService.parseCity(accessDO.getReq());
      GeocodeRsp geocodeRsp = geocodeService.queryGeocode(newCity);
      if (!newCity.contentEquals(geocodeRsp.getFormattedAddress())) {
        log.error(
            "content = {} new = {} old = {} location = {} formattedAddress = {}",
            accessDO.getReq(),
            newCity,
            accessDO.getAccessKey(),
            geocodeRsp.getLocation(),
            geocodeRsp.getFormattedAddress());
      }
    }
  }
}
