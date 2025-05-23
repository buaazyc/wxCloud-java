package com.tencent.wxcloudrun.controller.glow;

import com.tencent.wxcloudrun.client.glow.GlowService;
import com.tencent.wxcloudrun.client.qwen.AliService;
import com.tencent.wxcloudrun.constant.Constants;
import com.tencent.wxcloudrun.dao.AccessMapper;
import com.tencent.wxcloudrun.dataobject.AccessDO;
import com.tencent.wxcloudrun.entity.GlowEntity;
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
  private final AliService aliService;

  /**
   * 处理微信消息请求
   *
   * @param req 微信请求参数
   * @return 响应消息
   *
   */
  @PostMapping("/index")
  public WxResponse create(@RequestHeader Map<String, String> headers, @RequestBody WxRequest req) {
    long startTime = System.currentTimeMillis();
    WxResponse rsp = new WxResponse();
    if (req == null || req.getContent() == null) {
      return rsp;
    }
    log.info("headers={} req={}", headers, req);

    String address = aliService.parseCity(req.getContent());
    log.info("address after ai = {}", address);
    ArrayList<GlowEntity> glows = glowService.queryGlowWithFilter(address, false);
    String content = glowService.formatGlowStrRes(glows);

    // 构造返回rsp
    rsp.setToUserName(req.getFromUserName());
    rsp.setFromUserName(req.getToUserName());
    rsp.setCreateTime(req.getCreateTime());
    rsp.setContent(content);
    log.info("rsp = {}", rsp);

    // 记录访问日志
    if (!Constants.TEST_MSG_ID.equals(req.getMsgId())) {
      accessMapper.insertAccess(new AccessDO(headers, req, rsp, "glow", address));
    }
    log.info("cost={}s", (System.currentTimeMillis() - startTime)/1000);
    return rsp;
  }
}
