package com.tencent.wxcloudrun.client.GlowService;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.wxcloudrun.entity.Glow;
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

  public Glow toGlow() {
    Glow glow = new Glow();
    glow.setSummary(summary);
    glow.setPlaceHolder(placeHolder);
    glow.setAod(aod);
    glow.setEventTime(eventTime);
    glow.setQuality(quality);
    glow.setStatus(status);
    return glow;
  }
}
