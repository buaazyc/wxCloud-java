package com.tencent.wxcloudrun.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.tencent.wxcloudrun.client.GlowService;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dao.AccessMapper;
import com.tencent.wxcloudrun.dto.WxRequest;
import com.tencent.wxcloudrun.model.Access;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 微信消息处理控制器
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
  public ApiResponse create(@RequestHeader Map<String, String> headers,
      @RequestBody WxRequest req) {
    if (req == null) {
      return ApiResponse.error("无效的请求参数");
    }

    log.info("收到消息： {} {}", headers, req);

    // 根据经纬度获取天气信息
    String glowRes = glowService.getAll(req.getContent().trim());

    ApiResponse rsp = ApiResponse.wxMessage(
        req.getFromUserName(),
        req.getToUserName(),
        req.getCreateTime(),
        "text",
        glowRes);

    log.info("回复消息： {}", rsp);

    // 记录访问日志
    accessMapper.insertAccess(new Access(headers, req, rsp));
    return rsp;
  }
}
