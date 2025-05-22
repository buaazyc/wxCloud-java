package com.tencent.wxcloudrun.client.glow;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.wxcloudrun.entity.GlowEntity;
import lombok.Data;

/**
 * @author zhangyichuan
 * @date 2025/4/30
 */
@Data
public class GlowServiceRsp {
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

  public GlowEntity toGlow() {
    GlowEntity glow = new GlowEntity();
    glow.setSummary(summary);
    glow.setPlaceHolder(placeHolder);
    glow.setAod(aod);
    glow.setEventTime(eventTime);
    glow.setQuality(quality);
    glow.setStatus(status);
    return glow;
  }
}
