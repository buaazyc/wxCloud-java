package com.tencent.wxcloudrun.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.WxRequest;

/**
 * 微信消息处理控制器
 */
@RestController

public class IndexController {

  private final Logger logger;

  public IndexController() {
    this.logger = LoggerFactory.getLogger(IndexController.class);
  }

  /**
   * 处理微信消息请求
   * 
   * @param request 微信请求参数
   * @return 响应消息
   */
  @PostMapping(value = "/index")
  ApiResponse create(@RequestBody WxRequest request) {
    if (request == null) {
      logger.error("接收到空的微信请求");
      return ApiResponse.error("无效的请求参数");
    }
    logger.info("收到消息 {}", request);
    ApiResponse rsp = ApiResponse.wxMessage(
        request.getFromUserName(),
        request.getToUserName(),
        request.getCreateTime(),
        "text",
        "Hello World");
    logger.info("回复消息 {}", rsp);
    return rsp;
  }
}
