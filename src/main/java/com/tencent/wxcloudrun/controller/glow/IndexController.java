package com.tencent.wxcloudrun.controller.glow;

import com.tencent.wxcloudrun.client.qwen.AliService;
import com.tencent.wxcloudrun.constant.Constants;
import com.tencent.wxcloudrun.dao.AccessMapper;
import com.tencent.wxcloudrun.dataobject.AccessDO;
import com.tencent.wxcloudrun.manager.GlowManager;
import com.tencent.wxcloudrun.provider.WxRequest;
import com.tencent.wxcloudrun.provider.WxResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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

  private final AliService aliService;

  private final GlowManager glowManager;

  /**
   * 处理微信消息请求 依赖接口：
   *
   * <p>1. 阿里云qwen接口：解析城市
   *
   * <p>2. 高德geocode接口：根据城市获取经纬度
   *
   * <p>3. 地理云接口：查询火烧云情况
   *
   * <p>4. mysql：记录访问日志
   *
   * <p>5. 地理云接口：查询日出日落
   *
   * @param req 微信请求参数
   * @return 响应消息
   */
  @PostMapping("/index")
  public WxResponse create(@RequestHeader Map<String, String> headers, @RequestBody WxRequest req) {
    long startTime = System.currentTimeMillis();
    WxResponse rsp = new WxResponse();
    if (req == null || req.getContent() == null) {
      return rsp;
    }
    MDC.put("traceid", req.getMsgId());
    log.info("create req={}", req);

    String city = aliService.parseCity(req.getContent());
    log.info(
        "aliService parseCity content = {} city = {} cost = {}",
        req.getContent(),
        city,
        System.currentTimeMillis() - startTime);

    String content = glowManager.getGlow(city, false);

    rsp.setToUserName(req.getFromUserName());
    rsp.setFromUserName(req.getToUserName());
    rsp.setCreateTime(req.getCreateTime());
    rsp.setContent(content);
    log.info("glowManager getGlow cost = {} rsp = {}", System.currentTimeMillis() - startTime, rsp);

    // 记录访问日志
    if (!Constants.TEST.equals(req.getMsgId())) {
      accessMapper.insertAccess(new AccessDO(headers, req, rsp, "glow", city));
    }
    log.info("accessMapper insertAccess cost= {}", (System.currentTimeMillis() - startTime));
    return rsp;
  }
}
