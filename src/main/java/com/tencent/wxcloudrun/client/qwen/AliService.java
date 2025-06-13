package com.tencent.wxcloudrun.client.qwen;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tencent.wxcloudrun.dao.mapper.AccessMapper;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author zhangyichuan
 * @date 2025/5/21
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AliService {

  /** 缓存1month */
  private final Cache<String, String> cache =
      Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.DAYS).build();

  private final AccessMapper accessMapper;

  private static final String GLOW_SYS_CONTENT =
      "你是一个中国地理行政名称的专家，你可以检索高德的知识库"
          + "根据用户输入的内容，判断用户希望检索的地址，并给出该地址所在的或者对应的标准行政名称"
          + "输出格式为：xx省xx市xx县/区，三层结构。"
          + "如果是直辖市，则不需要省一级；"
          + "如果用户输入未包含区或县一级，输出格式为：xx省xx市，两层结构。"
          + "如果用户输入只到省一级，输出格式为：xx省，一层结构。"
          + "只输出最终的结果，不需要输出其他任何多余的文字。";

  private static final String SUN_GLOW_SYS_CONTENT =
      "你是一个中国地理行政名称的专家，你可以检索高德的知识库"
          + "请根据用户输入的地址，给出该地址的标准行政名称，层级xxx市级别"
          + "最终格式为xx市（不要有市的后缀），即没有'市'这个字眼"
          + "例如，用户输入大亚湾，那么你识别到这是指惠州大亚湾，那么你就输出到惠州这一层级，输出为惠州；"
          + "而用户输入南山区，你就直接输出深圳；如果用户输入洛阳，你就直接输出洛阳。"
          + "只输出最终的结果，不需要输出其他任何多余的文字。";

  public String parseCity(String inputContent) {
    long startTime = System.currentTimeMillis();
    String res = cache.getIfPresent(inputContent);
    if (res != null) {
      log.info(
          "parseCity cache hit, inputContent = {}, res = {}, cost = {}ms",
          inputContent,
          res,
          System.currentTimeMillis() - startTime);
      return res;
    }
    res = accessMapper.getAccessKey(inputContent);
    if (res != null) {
      log.info(
          "parseCity mysql hit, inputContent = {}, res = {}, cost = {}ms",
          inputContent,
          res,
          System.currentTimeMillis() - startTime);
      cache.put(inputContent, res);
      return res;
    }
    log.info("parseCity cache miss, content = {}", inputContent);

    res = callWithMessage(SUN_GLOW_SYS_CONTENT, inputContent);
    cache.put(inputContent, res);
    log.info(
        "parseCity inputContent = {}, res = {}, cost = {}ms",
        inputContent,
        res,
        System.currentTimeMillis() - startTime);
    return res;
  }

  private String callWithMessage(String sysContent, String content) {
    Generation gen = new Generation();
    Message systemMsg = Message.builder().role(Role.SYSTEM.getValue()).content(sysContent).build();
    Message userMsg = Message.builder().role(Role.USER.getValue()).content(content).build();
    GenerationParam param =
        GenerationParam.builder()
            .apiKey(System.getenv("QWEN_KEY"))
            // https://bailian.console.aliyun.com/?tab=model#/model-market/detail/qwen-max?modelGroup=qwen-max
            .model("qwen-max")
            .temperature(0f)
            .messages(Arrays.asList(systemMsg, userMsg))
            .resultFormat(GenerationParam.ResultFormat.MESSAGE)
            .build();
    try {
      GenerationResult result = gen.call(param);
      return result.getOutput().getChoices().get(0).getMessage().getContent();
    } catch (ApiException | NoApiKeyException | InputRequiredException e) {
      log.error("Failed to call API: {}", e.getMessage());
    }
    return "";
  }
}
