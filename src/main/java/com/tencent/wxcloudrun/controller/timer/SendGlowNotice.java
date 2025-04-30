package com.tencent.wxcloudrun.controller.timer;

import com.tencent.wxcloudrun.client.glow.GlowService;
import com.tencent.wxcloudrun.client.notify.NotifyService;
import com.tencent.wxcloudrun.dao.AccessMapper;
import com.tencent.wxcloudrun.dao.NotifyHistoryMapper;
import com.tencent.wxcloudrun.entity.Access;
import com.tencent.wxcloudrun.entity.NotifyHistory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
public class SendGlowNotice {
  private final AccessMapper accessMapper;
  private final NotifyHistoryMapper notifyHistoryMapper;
  private final GlowService glowService;
  private final NotifyService notifyService;

  /** 每1分钟执行一次 */
  @Scheduled(fixedRate = 60 * 1000)
  public void sendGlowNotice() {
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter stdFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    log.info("sendGlowNotice begin, now={}", now.format(stdFormatter));
    ArrayList<Access> accesses = accessMapper.getLastAccesses();
    for (Access access : accesses) {
      // 先插入sql表，标识已经发送，如果插入失败，则表明已经发送过，不再重复发送
      NotifyHistory notifyHistory =
          new NotifyHistory(now.format(dateFormatter), access.getFromUserName(), access.getReq());
      if (!notifyHistoryMapper.insertNotifyHistory(notifyHistory)) {
        log.info("sendGlowNotice already sent {}", notifyHistory);
        continue;
      }

      // 查询火烧云情况
      String glowRes = glowService.queryGlowStrRes(access.getReq(), true);
      if ("".equals(glowRes)) {
        continue;
      }

      // 调用发送接口发送给用户
      log.info("sendGlowNotice user={} glowRes={}", notifyHistory.getUser(), glowRes);
      notifyService.sendNotify(notifyHistory.getUser(), glowRes);
    }
  }
}
