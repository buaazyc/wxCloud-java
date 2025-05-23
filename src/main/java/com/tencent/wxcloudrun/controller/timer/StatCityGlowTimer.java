package com.tencent.wxcloudrun.controller.timer;

import com.tencent.wxcloudrun.client.email.EmailService;
import com.tencent.wxcloudrun.client.glow.GlowService;
import com.tencent.wxcloudrun.client.qwen.AliService;
import com.tencent.wxcloudrun.dao.AccessMapper;
import com.tencent.wxcloudrun.entity.GlowEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

  private final GlowService glowService;

  private final EmailService emailService;

  private final AliService aliService;

  @PostConstruct
  public void runOnceOnStartup() {
//    executeTask();
  }

  @Scheduled(cron = "0 40 11 * * *", zone = "Asia/Shanghai")
  public void scheduledTask() {
    executeTask();
  }

  public void executeTask() {
    StringBuilder subject = new StringBuilder();
    for (String city : accessMapper.getCityList()) {
      // 查询火烧云情况
      // 保护接口，每个城市查询后休眠0.1秒
      try {
        Thread.sleep(100);
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
    emailService.sendEmail(System.getenv("EMAIL_TO"), "火烧云情况", subject.toString());
    log.info("statCityGlow end, now is {}, stat res = {}", LocalDateTime.now(), subject);
  }
}
