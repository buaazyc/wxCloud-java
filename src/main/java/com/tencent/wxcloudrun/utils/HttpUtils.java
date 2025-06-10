package com.tencent.wxcloudrun.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

/**
 * @author zhangyichuan
 * @date 2025/6/6
 */
public class HttpUtils {

  public static HttpEntity<String> getStringHttpEntity() {
    HttpHeaders headers = new HttpHeaders();
    // 模拟Chrome浏览器（基于macOS）
    headers.set(
        "User-Agent",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36");
    headers.set("Accept", "*/*");
    headers.set("Accept-Language", "zh-CN,zh;q=0.9");
    headers.set("Accept-Encoding", "gzip, deflate, br, zstd");
    headers.set(
        "Sec-Ch-Ua",
        "\"Chromium\";v=\"136\", \"Google Chrome\";v=\"136\", \"Not.A/Brand\";v=\"99\"");
    headers.set("Sec-Ch-Ua-Mobile", "?0");
    headers.set("Sec-Ch-Ua-Platform", "\"macOS\"");
    headers.set("Sec-Fetch-Dest", "empty");
    headers.set("Sec-Fetch-Mode", "cors");
    headers.set("Sec-Fetch-Site", "same-origin");
    headers.set("X-Requested-With", "XMLHttpRequest");
    headers.set("Connection", "keep-alive");
    headers.set("Priority", "u=1, i");
    // 创建带请求头的请求实体
    return new HttpEntity<>(headers);
  }
}
