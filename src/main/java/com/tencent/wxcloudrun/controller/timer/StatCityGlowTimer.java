package com.tencent.wxcloudrun.controller.timer;

import com.tencent.wxcloudrun.client.email.EmailService;
import com.tencent.wxcloudrun.dao.mapper.AccessMapper;
import com.tencent.wxcloudrun.domain.constant.Constants;
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

  @PostConstruct
  public void runOnceOnStartup() {
    // 测试环境，立即执行
    if (Constants.TEST.equals(System.getenv(Constants.ENV))) {
      //      checkBeautifulGlowWithEmail();
    }
  }

  /** 定时统计火烧云情况，并发送邮件 */
  @Scheduled(cron = "0 12 12 * * *", zone = "Asia/Shanghai")
  public void dayCronTask() {
    // 测试环境执行，正式环境不跑了，避免量api配额消耗过多
    if (Constants.TEST.equals(System.getenv(Constants.ENV))) {
      checkBeautifulGlowWithEmail();
    }
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

      String content = glowManager.getGlow(city, true);

      if ("".equals(content)) {
        continue;
      }
      // 满足过滤条件后，则为优质火烧云，发送邮件推送到管理员
      log.info("city is {}, glowRes is beautiful {}", city, content);
      subject.append(content).append("---------------").append("\n");
    }
    if (subject.length() == 0) {
      log.info("no beautiful glow");
      return;
    }
    emailService.sendEmail(System.getenv("EMAIL_TO"), "火烧云情况", subject.toString());
    log.info("statCityGlow end stat res = {}", subject);
  }
}
