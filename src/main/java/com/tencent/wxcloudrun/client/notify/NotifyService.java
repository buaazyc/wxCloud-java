package com.tencent.wxcloudrun.client.notify;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author zhangyichuan
 * @date 2025/4/30
 */
@Slf4j
@Service
public class NotifyService {
  @Autowired private RestTemplate restTemplate;

  private static final String URL = "http://api.weixin.qq.com/cgi-bin/message/custom/send";

  public void sendNotify(String user, String content) {
    NotifyServiceReq req = new NotifyServiceReq(user, content);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<NotifyServiceReq> entity = new HttpEntity<>(req, headers);
    ResponseEntity<NotifyServiceRsp> response =
        restTemplate.postForEntity(URL, entity, NotifyServiceRsp.class);
    if (!response.getStatusCode().is2xxSuccessful()) {
      log.error("notifyServiceRsp is not ok {}", response.getStatusCode());
      return;
    }
    NotifyServiceRsp body = response.getBody();
    if (body == null) {
      log.error("notifyServiceRsp is null");
      return;
    }
    if (!body.ok()) {
      log.error("notifyServiceRsp is not ok {}", body);
    }
  }
}
