package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.client.GlowService.GlowService;
import com.tencent.wxcloudrun.dao.AccessMapper;
import com.tencent.wxcloudrun.entity.Access;
import com.tencent.wxcloudrun.entity.Glow;
import com.tencent.wxcloudrun.provider.WxRequest;
import com.tencent.wxcloudrun.provider.WxResponse;
import java.util.ArrayList;
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

    // 根据经纬度获取天气信息
    ArrayList<Glow> glowRes = glowService.queryGlowRes(req.getContent());
    if (!glowRes.get(0).ok()) {
      return rsp;
    }

    // 组装结果
    StringBuilder content = new StringBuilder(glowRes.get(0).getFormattedSummary() + "火烧云情况\n");
    for (Glow glow : glowRes) {
      content.append("\n").append(glow.format());
    }

    // 构造返回rsp
    rsp.setToUserName(req.getFromUserName());
    rsp.setFromUserName(req.getToUserName());
    rsp.setCreateTime(req.getCreateTime());
    rsp.setContent(content.toString());
    log.info("rsp={}", rsp);

    // 记录访问日志
    accessMapper.insertAccess(new Access(headers, req, rsp));
    return rsp;
  }
}
