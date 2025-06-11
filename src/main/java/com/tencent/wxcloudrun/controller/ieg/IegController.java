package com.tencent.wxcloudrun.controller.ieg;

import com.tencent.wxcloudrun.client.email.EmailService;
import com.tencent.wxcloudrun.dao.mapper.IegUserMapper;
import com.tencent.wxcloudrun.dao.dataobject.IegUserDO;
import com.tencent.wxcloudrun.service.provider.WxRequest;
import com.tencent.wxcloudrun.service.provider.WxResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhangyichuan
 * @date 2025/5/22
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/ieg")
public class IegController {

  private final EmailService emailService;

  private final IegUserMapper iegUserMapper;

  @PostMapping("/index")
  public WxResponse create(@RequestHeader Map<String, String> headers, @RequestBody WxRequest req) {
    WxResponse rsp = new WxResponse();
    if (req == null || req.getContent() == null) {
      return rsp;
    }
    log.info("create req={}", req);
    rsp.setToUserName(req.getFromUserName());
    rsp.setFromUserName(req.getToUserName());
    rsp.setCreateTime(req.getCreateTime());

    // 从req中解析出收件人
    String[] parts = splitIntoTwoParts(req.getContent());
    log.info("splitIntoTwoParts req.getContent() = {} parts = {}", req.getContent(), parts);

    // 解析失败，则回复调整格式
    int contentPart = 2;
    if (parts.length != contentPart || "".equals(parts[0]) || "".equals(parts[1])) {
      rsp.setContent("格式错误，请调整格式：第一行为收件人笔名，第二行开始为留言内容");
      return rsp;
    }

    // 根据收件人，查询db，获取email地址
    IegUserDO iegUserDO = iegUserMapper.getUserByName(parts[0]);
    // 查不到收件，则回复收件人不存在
    if (iegUserDO == null || "".equals(iegUserDO.getEmail())) {
      rsp.setContent("收件人不存在。请确保格式正确：第一行为收件人笔名，第二行开始为留言内容");
      return rsp;
    }
    log.info("iegUserDO = {}", iegUserDO);

    // 加一个本地流控

    // 发送邮件
    emailService.sendEmail(iegUserDO.getEmail(), "IEG读书分享会（七夕活动）新的留言，请勿回复", parts[1]);

    // 回复消息发送成功
    rsp.setContent("已成功将留言发送给" + parts[0] + "。");
    return rsp;
  }

  private String[] splitIntoTwoParts(String input) {
    if (input == null || input.isEmpty()) {
      // 处理空或 null 输入
      return new String[] {"", ""};
    }
    // 按换行符最多分割一次
    String[] result = input.split("\n", 2);
    // 如果没有找到换行符，则第二部分为空
    if (result.length == 1) {
      return new String[] {result[0], ""};
    }
    return result;
  }
}
