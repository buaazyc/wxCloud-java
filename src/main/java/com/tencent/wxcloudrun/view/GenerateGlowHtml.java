package com.tencent.wxcloudrun.view;

import com.tencent.wxcloudrun.domain.entity.GlowEntity;
import gui.ava.html.parser.HtmlParser;
import gui.ava.html.parser.HtmlParserImpl;
import gui.ava.html.renderer.ImageRenderer;
import gui.ava.html.renderer.ImageRendererImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * @author zhangyichuan
 * @date 2025/7/2
 */
@Service
@Slf4j
public class GenerateGlowHtml {

  private final TemplateEngine templateEngine;

  public GenerateGlowHtml() {
    this.templateEngine = new TemplateEngine();
    ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
    resolver.setPrefix("/templates/");
    resolver.setSuffix(".html");
    resolver.setTemplateMode("HTML");
    templateEngine.setTemplateResolver(resolver);
  }

  public String generateHtmlContent(ArrayList<GlowEntity> glows) {
    String htmlContent = templateEngine.process("glowTemplate", getContext(glows));

    // 写入 HTML 文件到本地
    // todo: 删除文件
    java.nio.file.Path path = java.nio.file.Paths.get("output.html");
    try {
      java.nio.file.Files.write(
          path, htmlContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    } catch (Exception e) {
      log.error("Error writing HTML file: {}", e.getMessage(), e);
    }

    // 创建 HtmlParser 实例并加载 HTML 内容
    HtmlParser htmlParser = new HtmlParserImpl();
    htmlParser.loadHtml(htmlContent);

    // 创建 ImageRenderer 实例并设置解析器
    ImageRenderer imageRenderer = new ImageRendererImpl(htmlParser);

    imageRenderer.setHeight(700);
    imageRenderer.setWidth(400);

    // 将 HTML 渲染为 PNG 图像并保存到文件
    imageRenderer.saveImage(MDC.get("traceid") + ".png");

    log.info("save success");
    return htmlContent;
  }

  private static @NotNull Context getContext(ArrayList<GlowEntity> glows) {
    Context context = new Context();
    context.setVariable("summary", glows.isEmpty() ? "" : glows.get(0).getFormattedSummary());
    List<Map<String, Object>> glowList = new ArrayList<>();

    for (GlowEntity glow : glows) {
      Map<String, Object> map = new HashMap<>();
      map.put("eventTime", glow.getFormattedEventTime());
      map.put("qualityStr", glow.getStrQuality());
      map.put("aodStr", glow.getStrAod());
      map.put("beautiful", glow.isBeautiful());
      map.put("aodLevel", glow.getNumAod());
      glowList.add(map);
    }
    context.setVariable("glows", glowList);
    return context;
  }
}
