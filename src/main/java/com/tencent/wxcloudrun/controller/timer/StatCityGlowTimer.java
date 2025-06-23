package com.tencent.wxcloudrun.controller.timer;

import com.tencent.wxcloudrun.client.email.EmailService;
import com.tencent.wxcloudrun.client.self.SelfService;
import com.tencent.wxcloudrun.client.sun.SunGlowService;
import com.tencent.wxcloudrun.dao.mapper.AccessMapper;
import com.tencent.wxcloudrun.dao.mapper.CityMapper;
import com.tencent.wxcloudrun.service.manager.GlowManager;
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

  private final GlowManager glowManager;

  private final SunGlowService sunGlowService;

  private final CityMapper cityMapper;

  private final SelfService selfService;

  /** 刷新火烧云每个城市的刷新间隔：30s */
  private static final Long REFRESH_GLOW_TIME = 30 * 1000L;

  @PostConstruct
  public void runOnceOnStartup() {
    //    selfService.keepAlive();
  }

  /** 定时统计火烧云情况，并发送邮件 */
  @Scheduled(cron = "0 13 12 * * *", zone = "Asia/Shanghai")
  public void dayCronTask() {
    //    checkBeautifulGlowWithEmail();
  }

  /** 定时刷新火烧云情况 */
  @Scheduled(cron = "12 40 * * * *", zone = "Asia/Shanghai")
  public void refreshGlowCronTask() {
    //    refreshGlow();
  }

  /** 每分钟运行，保证服务器不会关机 */
  @Scheduled(cron = "0 * * * * *", zone = "Asia/Shanghai")
  public void statPvUv() {
    log.info("today access pv = {} uv = {}", accessMapper.getTodayPv(), accessMapper.getTodayUv());
  }

  public void checkBeautifulGlowWithEmail() {
    StringBuilder subject = new StringBuilder();
    for (String city : accessMapper.getCityList()) {
      // 查询火烧云情况
      String content =
          sunGlowService.formatGlowStrRes(sunGlowService.queryGlowWithFilter(city, true));

      if ("".equals(content)) {
        continue;
      }
      // 满足过滤条件后，则为优质火烧云，发送邮件推送到管理员
      log.info("city is {}, glowRes is beautiful {}", city, content);
      subject.append(content).append("---------------").append("\n");

      // 保护接口，每个城市查询后休眠一段时间
      try {
        Thread.sleep(REFRESH_GLOW_TIME);
      } catch (InterruptedException e) {
        log.error("sleep error", e);
      }
    }
    if (subject.length() == 0) {
      log.info("no beautiful glow");
      return;
    }
    emailService.sendEmail(System.getenv("EMAIL_TO"), "火烧云情况", subject.toString());
    log.info("statCityGlow end stat res = {}", subject);
  }

  public void refreshGlow() {
    for (String city : accessMapper.getCityList()) {
      // 查询火烧云情况
      String content =
          sunGlowService.formatGlowStrRes(sunGlowService.queryGlowWithFilter(city, false));
      log.info("refreshGlow glowRes is {}", content);

      // 保护接口，每个城市查询后休眠一段时间
      try {
        Thread.sleep(REFRESH_GLOW_TIME);
      } catch (InterruptedException e) {
        log.error("sleep error", e);
      }
    }
  }
}
