package com.tencent.wxcloudrun.manager;

import com.tencent.wxcloudrun.client.geocode.GeocodeRsp;
import com.tencent.wxcloudrun.client.geocode.GeocodeService;
import com.tencent.wxcloudrun.client.glow.GlowService;
import com.tencent.wxcloudrun.client.solar.SolarRsp;
import com.tencent.wxcloudrun.client.solar.SolarService;
import com.tencent.wxcloudrun.entity.GlowEntity;
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
    GeocodeRsp geocodeRsp = geocodeService.queryGeocode(city);
    GlowEntity glow = glowService.queryGlow(geocodeRsp.getLocation());
    SolarRsp solar = solarService.querySolar(geocodeRsp.getLocation());
    glow.setSunTime(solar);
    glow.setAddress(geocodeRsp.getFormattedAddress());
    if (filter) {
      return glow.emailFormatWithFilter();
    }
    return glow.messageFormat();
  }
}
