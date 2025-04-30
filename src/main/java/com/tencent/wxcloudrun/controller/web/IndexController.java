package com.tencent.wxcloudrun.controller.web;

import com.tencent.wxcloudrun.client.glow.GlowService;
import com.tencent.wxcloudrun.client.notify.NotifyService;
import com.tencent.wxcloudrun.dao.AccessMapper;
import com.tencent.wxcloudrun.entity.Access;
import com.tencent.wxcloudrun.provider.WxRequest;
import com.tencent.wxcloudrun.provider.WxResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微信消息处理控制器
 *
 * @author zhangyichuan
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class IndexController {

  private final AccessMapper accessMapper;
  private final GlowService glowService;
  private final NotifyService notifyService;

  /**
   * 处理微信消息请求
   *
   * @param req 微信请求参数
   * @return 响应消息
   */
  @PostMapping("/index")
  public WxResponse create(@RequestHeader Map<String, String> headers, @RequestBody WxRequest req) {
    WxResponse rsp = new WxResponse();
    if (req == null || req.getContent() == null) {
      return rsp;
    }
    log.info("headers={} req={}", headers, req);

    if ("oBY566s96Ou1Yn16HdbxCfh_wW5c".equals(req.getFromUserName())
        && "test".equals(req.getContent())) {
      notifyService.sendNotify(req.getFromUserName(), "test");
      return rsp;
    }

    // 构造返回rsp
    rsp.setToUserName(req.getFromUserName());
    rsp.setFromUserName(req.getToUserName());
    rsp.setCreateTime(req.getCreateTime());
    rsp.setContent(glowService.queryGlowStrRes(req.getContent(), false));
    log.info("rsp={}", rsp);

    // 记录访问日志
    accessMapper.insertAccess(new Access(headers, req, rsp));
    return rsp;
  }
}
