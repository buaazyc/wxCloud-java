package com.tencent.wxcloudrun.controller.ieg;

import com.tencent.wxcloudrun.client.email.EmailService;
import com.tencent.wxcloudrun.dao.dataobject.IegUserDO;
import com.tencent.wxcloudrun.dao.mapper.IegUserMapper;
import com.tencent.wxcloudrun.service.provider.WxRequest;
import com.tencent.wxcloudrun.service.provider.WxResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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
    MDC.put("traceid", req.getMsgId());
    rsp.setToUserName(req.getFromUserName());
    rsp.setFromUserName(req.getToUserName());
    rsp.setCreateTime(req.getCreateTime());

    // 从req中解析出收件人
    String[] parts = splitIntoTwoParts(req.getContent());
    log.info("splitIntoTwoParts req.getContent() = {} parts = {}", req.getContent(), parts);

    // 解析失败，则回复调整格式
    int contentPart = 2;
    if (parts.length != contentPart || "".equals(parts[0]) || "".equals(parts[1])) {
      rsp.setContent("格式错误，请调整格式。\n" + helper());
      return rsp;
    }

    // 根据收件人，查询db，获取email地址
    IegUserDO iegUserDO = iegUserMapper.getUserByName(parts[0]);
    // 查不到收件，则回复收件人不存在
    if (iegUserDO == null || "".equals(iegUserDO.getEmail())) {
      rsp.setContent("收件人不存在，请确保格式正确。\n" + helper());
      return rsp;
    }
    log.info("iegUserDO = {}", iegUserDO);

    // 加一个本地流控

    // 发送邮件
    emailService.sendEmailForIeg(iegUserDO.getEmail(), parts[1]);

    // 回复消息发送成功
    rsp.setContent("已成功将留言发送给【" + parts[0] + "】");
    return rsp;
  }

  private String[] splitIntoTwoParts(String input) {
    if (input == null || input.isEmpty()) {
      // 处理空或 null 输入
      return new String[] {"", ""};
    }
    // 按换行符最多分割一次
    String[] result = input.split("，", 2);
    // 如果没有找到换行符，则第二部分为空
    if (result.length == 1) {
      return new String[] {result[0], ""};
    }
    return result;
  }

  private String helper() {
    return "输入格式为：收件人笔名，留言内容。\n"
        + "例如：张三，你书单中的《三体》我也很喜欢。\n"
        + "注意：\n"
        + "1. 收件人笔名必须与公布的书单列表中的完全一致；\n"
        + "2. 请使用“中文逗号”分隔笔名和留言；\n"
        + "3. 内容中不要包含表情，邮件无法正常展示微信表情；\n"
        + "4. 邮件发送人为协会统一邮箱，收件人无法知道真正的收件人；\n"
        + "5. 如果收到留言，请勿直接回复邮件。\n";
  }
}
