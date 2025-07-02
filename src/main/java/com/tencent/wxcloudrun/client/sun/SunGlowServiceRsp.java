package com.tencent.wxcloudrun.client.sun;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.wxcloudrun.domain.entity.SunGlowEntity;
import lombok.Data;

/**
 * @author zhangyichuan
 * @date 2025/4/30
 */
@Data
public class SunGlowServiceRsp {
  @JsonProperty("img_summary")
  private String summary;

  @JsonProperty("place_holder")
  private String placeHolder;

  @JsonProperty("tb_aod")
  private String aod;

  @JsonProperty("tb_event_time")
  private String eventTime;

  @JsonProperty("tb_quality")
  private String quality;

  @JsonProperty("status")
  private String status;

  public SunGlowEntity toGlow() {
    SunGlowEntity glow = new SunGlowEntity();
    glow.setSummary(summary);
    glow.setPlaceHolder(placeHolder);
    glow.setAod(aod);
    glow.setEventTime(eventTime);
    glow.setQuality(quality);
    glow.setStatus(status);
    return glow;
  }
}
