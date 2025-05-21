package com.tencent.wxcloudrun.client.qwen;

import java.util.Arrays;
import java.lang.System;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * @author zhangyichuan
 * @date 2025/5/21
 */
@Slf4j
@Component
public class AliService {
    public String callWithMessage(String content)  {
        Generation gen = new Generation();
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content("你是一个专业的气象学家，并且很会写公众号文章。现在给你预测的火烧云情况，写一段公众号文章，使用text格式。")
                .build();
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content(content)
                .build();
        GenerationParam param = GenerationParam.builder()
                .apiKey(System.getenv("QWEN_KEY"))
                .model("qwen-max")
                .messages(Arrays.asList(systemMsg, userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();
        try {
            log.info("content = {}, res is gen by qwen...", content);
            GenerationResult result = gen.call(param);
            return result.getOutput().getChoices().get(0).getMessage().getContent();
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            log.error("Failed to call API: {}", e.getMessage());
        }
        return "";
    }
}
