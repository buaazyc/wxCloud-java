package com.tencent.wxcloudrun.client.qwen;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import java.util.Arrays;
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
                .content("你是一个中国地理行政名称的专家，你可以检索高德的知识库" +
                        "请根据用户输入的地址，给出该地址的标准行政名称，层级xxx市级别" +
                        "最终格式为xx市（不要有市的后缀），即没有'市'这个字眼" +
                        "例如，用户输入大亚湾，那么你识别到这是指惠州大亚湾，那么你就输出到惠州这一层级，输出为惠州；" +
                        "而用户输入南山区，你就直接输出深圳；如果用户输入洛阳，你就直接输出洛阳。" +
                        "只输出最终的结果，不需要输出其他任何多余的文字。")
                .build();
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content(content)
                .build();
        GenerationParam param = GenerationParam.builder()
                .apiKey(System.getenv("QWEN_KEY"))
                .model("qwen-max")
                .temperature(0f)
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
