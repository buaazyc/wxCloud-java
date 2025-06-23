package com.tencent.wxcloudrun.domain.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

/**
 * @author zhangyichuan
 * @date 2025/6/6
 */
public class HttpUtils {

  public static HttpEntity<String> getStringHttpEntity() {
    HttpHeaders headers = new HttpHeaders();

    // 完全匹配浏览器抓包的 headers
    headers.set(
        "User-Agent",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36");

    headers.set(
        "Accept",
        "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");

    headers.set("Accept-Language", "zh-CN,zh;q=0.9");
    headers.set("Accept-Encoding", "gzip, deflate, br, zstd");

    headers.set(
        "Sec-Ch-Ua",
        "\"Google Chrome\";v=\"137\", \"Chromium\";v=\"137\", \"Not/A)Brand\";v=\"24\"");

    headers.set("Sec-Ch-Ua-Mobile", "?0");
    headers.set("Sec-Ch-Ua-Platform", "\"macOS\"");

    headers.set("Sec-Fetch-Dest", "document");
    headers.set("Sec-Fetch-Mode", "navigate");
    headers.set("Sec-Fetch-Site", "none");
    headers.set("Sec-Fetch-User", "?1");

    headers.set("Upgrade-Insecure-Requests", "1");

    // Cookie 需要动态处理，这里提供示例值
    headers.set("Cookie", "region_name=中山东; city_name=万宁");

    return new HttpEntity<>(headers);
  }
}
