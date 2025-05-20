package com.tencent.wxcloudrun.controller.timer;

import com.tencent.wxcloudrun.client.email.EmailService;
import com.tencent.wxcloudrun.client.glow.GlowService;
import com.tencent.wxcloudrun.dao.AccessMapper;
import com.tencent.wxcloudrun.entity.Glow;
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
    StringBuilder subject = new StringBuilder();
    for (String city : accessMapper.getCityList()) {
      // 查询火烧云情况
      // 保护接口，先休眠0.1秒
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        log.error("sleep error", e);
      }
      // 增加过滤
      ArrayList<Glow> glows = glowService.queryGlowWithFilter(city ,true);
      String glowRes = glowService.formatGlowStrRes(glows);
      if ("".equals(glowRes)) {
        continue;
      }
      // 满足过滤条件后，则为优质火烧云，发送邮件推送到管理员
      log.info("city is {}, glowRes is beautiful {}", city, glowRes);
      subject.append(glowRes).append("-----------------------------------------").append("\n");
    }
    emailService.sendEmail(System.getenv("EMAIL_TO"), "火烧云情况", subject.toString());
    log.info("statCityGlow end, now is {}", LocalDateTime.now());
  }
}
