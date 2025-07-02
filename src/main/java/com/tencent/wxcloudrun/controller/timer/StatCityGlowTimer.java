package com.tencent.wxcloudrun.controller.timer;

import com.tencent.wxcloudrun.client.email.EmailService;
import com.tencent.wxcloudrun.client.sun.GlowService;
import com.tencent.wxcloudrun.dao.mapper.AccessMapper;
import com.tencent.wxcloudrun.domain.constant.Constants;
import com.tencent.wxcloudrun.domain.constant.QueryGlowTypeEnum;
import com.tencent.wxcloudrun.domain.entity.QueryGlowEntity;
import java.util.List;
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

  private final GlowService glowService;

  @PostConstruct
  public void runOnceOnStartup() {
    if (Constants.TEST.equals(System.getenv(Constants.ENV))) {
      checkBeautifulGlowWithEmail();
    }
  }

  /** 每分钟运行，保证服务器不会关机 */
  @Scheduled(cron = "0 * * * * *", zone = "Asia/Shanghai")
  public void statPvUv() {
    log.info("today access pv = {} uv = {}", accessMapper.getTodayPv(), accessMapper.getTodayUv());
  }

  public void checkBeautifulGlowWithEmail() {
    StringBuilder subject = new StringBuilder();
    List<String> cityList = accessMapper.getCityList();
    for (int i = 0; i < cityList.size(); i++) {
      String city = cityList.get(i);
      log.info("index is {}/{}, city is {}", i, cityList.size(), city);
      // 查询火烧云情况
      QueryGlowEntity queryGlowEntity = new QueryGlowEntity(city, QueryGlowTypeEnum.STAT);
      String content = glowService.formatGlowStatStrRes(glowService.queryGlow(queryGlowEntity));

      // 保护接口，每个城市查询后休眠10s时间
      try {
        Thread.sleep(5 * 1000);
      } catch (InterruptedException e) {
        log.error("sleep error", e);
      }

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
