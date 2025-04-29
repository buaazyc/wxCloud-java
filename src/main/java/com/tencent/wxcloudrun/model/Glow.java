package com.tencent.wxcloudrun.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.Data;

/**
 * @author zhangyichuan
 */
@Data
public class Glow implements Serializable {
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

  public boolean ok() {
    return "ok".equals(status);
  }

  /**
   * 获取格式化后的事件
   *
   * @return 格式化后的事件
   */
  public String getFormattedEventTime() {
    return eventTime.replace("<br>", " ");
  }

  /**
   * 获取格式化后的质量
   *
   * @return 格式化后的质量
   */
  public String getFormattedQuality() {
    return quality.replace("<br>", " ").replace("\n", "");
  }

  /**
   * 获取格式化后的摘要
   *
   * @return 格式化后的摘要
   */
  public String getFormattedSummary() {
    String cleanText = summary.replace("&ensp;", "").replace("<b>", "").replace("</b>", "");
    // 按】分割并获取第一部分,加上】
    return cleanText.split("】")[0] + "】";
  }

  public String format() {
    return getFormattedEventTime() + "\n质量：" + getFormattedQuality();
  }
}
