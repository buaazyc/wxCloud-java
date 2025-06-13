package com.tencent.wxcloudrun.domain.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * @author zhangyichuan
 */
@Configuration
public class AppConfig {
  @Bean
  public RestTemplate restTemplate() {
    // 创建支持自动解压 GZIP 的 HttpClient 实例
    CloseableHttpClient httpClient = HttpClients.custom().setUserAgent("your-user-agent").build();

    // 创建请求工厂并启用自动解压 GZIP 响应
    HttpComponentsClientHttpRequestFactory factory =
        new HttpComponentsClientHttpRequestFactory(httpClient);

    // 创建自定义的 ObjectMapper 来过滤非法字符
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    RestTemplate restTemplate = new RestTemplate(factory);
    restTemplate.getMessageConverters().set(0, new MappingJackson2HttpMessageConverter(mapper));

    return restTemplate;
  }
}
