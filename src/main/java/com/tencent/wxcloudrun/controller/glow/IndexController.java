package com.tencent.wxcloudrun.controller.glow;

import com.tencent.wxcloudrun.client.qwen.AliService;
import com.tencent.wxcloudrun.client.sun.SunGlowService;
import com.tencent.wxcloudrun.dao.dataobject.AccessDO;
import com.tencent.wxcloudrun.dao.dataobject.CityDO;
import com.tencent.wxcloudrun.dao.mapper.AccessMapper;
import com.tencent.wxcloudrun.dao.mapper.CityMapper;
import com.tencent.wxcloudrun.domain.constant.Constants;
import com.tencent.wxcloudrun.service.manager.GlowManager;
import com.tencent.wxcloudrun.service.provider.WxRequest;
import com.tencent.wxcloudrun.service.provider.WxResponse;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

  private final ExecutorService executor = Executors.newFixedThreadPool(10);

  private final AccessMapper accessMapper;

  private final CityMapper cityMapper;

  private final AliService aliService;

  private final GlowManager glowManager;

  private final SunGlowService sunGlowService;

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
   * <p>响应时间必须控制在5s以内
   *
   * @param req 微信请求参数
   * @return 响应消息
   */
  @PostMapping("/index")
  public WxResponse index(@RequestHeader Map<String, String> headers, @RequestBody WxRequest req) {
    long startTime = System.currentTimeMillis();
    WxResponse rsp = new WxResponse();
    if (req == null || req.getContent() == null) {
      return rsp;
    }
    if (Constants.TEST.equals(req.getMsgId())) {
      req.setMsgId(req.getMsgId() + "_" + System.currentTimeMillis());
    }
    MDC.put("traceid", req.getMsgId());
    log.info("req={}", req);

    String city = aliService.parseCity(req.getContent());

    String content =
        sunGlowService.formatGlowStrRes(sunGlowService.queryGlowWithFilter(city, false));

    rsp.setToUserName(req.getFromUserName());
    rsp.setFromUserName(req.getToUserName());
    rsp.setCreateTime(req.getCreateTime());
    rsp.setContent(content);

    // 统计耗时，打印结果
    Long cost = System.currentTimeMillis() - startTime;
    if (cost > Constants.TIME_OUT) {
      log.error("cost= {}ms , rsp = {}", cost, rsp);
    } else {
      log.info("cost= {}ms , rsp = {}", cost, rsp);
    }

    // 异步执行
    executor.submit(
        () -> {
          MDC.put("traceid", req.getMsgId());
          long startInsetAccess = System.currentTimeMillis();

          try {
            // 记录访问记录
            accessMapper.insertAccess(new AccessDO(headers, req, rsp, "glow", city));
            // 如果查询结果不为空，记录城市
            if (!"".equals(content)) {
              cityMapper.insertCity(new CityDO(req.getContent(), city));
            }
            log.info("async success cost = {}ms", System.currentTimeMillis() - startInsetAccess);
          } catch (Exception e) {
            log.error("insertAccess failed with exception: ", e);
          }
        });

    return rsp;
  }
}
