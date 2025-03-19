package com.tencent.wxcloudrun.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dao.AccessMapper;
import com.tencent.wxcloudrun.dto.WxRequest;
import com.tencent.wxcloudrun.model.Access;

import lombok.RequiredArgsConstructor;

/**
 * 微信消息处理控制器
 */
@RestController
@RequiredArgsConstructor

public class IndexController {

  private static final Logger logger = LoggerFactory.getLogger(IndexController.class);
  private final AccessMapper accessMapper;

  /**
   * 处理微信消息请求
   * 
   * @param req 微信请求参数
   * @return 响应消息
   */
  @PostMapping("/index")
  public ApiResponse create(@RequestHeader Map<String, String> headers, @RequestBody WxRequest req) {
    if (req == null) {
      logger.error("接收到空的微信请求");
      return ApiResponse.error("无效的请求参数");
    }

    logger.info("收到消息： {} {}", headers, req);

    ApiResponse rsp = ApiResponse.wxMessage(
        req.getFromUserName(),
        req.getToUserName(),
        req.getCreateTime(),
        "text",
        req.getContent());

    logger.info("回复消息： {}", rsp);

    // 记录访问日志
    accessMapper.insertAccess(new Access(headers, req, rsp));
    return rsp;
  }
}
