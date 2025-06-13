package com.tencent.wxcloudrun.service.manager;

import com.tencent.wxcloudrun.client.geocode.GeocodeService;
import com.tencent.wxcloudrun.client.glow.GlowService;
import com.tencent.wxcloudrun.client.solar.SolarRsp;
import com.tencent.wxcloudrun.client.solar.SolarService;
import com.tencent.wxcloudrun.domain.entity.GlowEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangyichuan
 * @date 2025/6/9
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class GlowManager {
  private final GlowService glowService;

  private final GeocodeService geocodeService;

  private final SolarService solarService;

  public String getGlow(String city, boolean filter) {
    String location = geocodeService.queryGeocode(city);
    if (location == null) {
      return "系统错误：未找到该城市信息";
    }
    GlowEntity glow = glowService.queryGlow(location);
    if (glow == null) {
      return "系统错误：未找到该城市火烧云预报";
    }
    SolarRsp solar = solarService.querySolar(location);
    if (solar == null) {
      return "系统错误：未找到该城市日出日落时间";
    }
    glow.setSunTime(solar);
    glow.setAddress(city);
    if (filter) {
      return glow.emailFormatWithFilter();
    }
    return glow.messageFormat();
  }
}
