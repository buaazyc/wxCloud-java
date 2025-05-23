package com.tencent.wxcloudrun.controller.ieg;

import com.tencent.wxcloudrun.client.email.EmailService;
import com.tencent.wxcloudrun.provider.WxRequest;
import com.tencent.wxcloudrun.provider.WxResponse;
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

    private final EmailService  emailService;

    @PostMapping("/index")
    public WxResponse create(@RequestHeader Map<String, String> headers, @RequestBody WxRequest req) {
        WxResponse rsp = new WxResponse();
        if (req == null || req.getContent() == null) {
            return rsp;
        }
        // 从req中解析出收件人

        // 解析失败，则回复调整格式

        // 根据收件人，查询db，获取email地址

        // 查不到收件，则回复收件人不存在

        // 发送邮件

        // 发送失败，则回复联系管理员

        return rsp;
    }
}
