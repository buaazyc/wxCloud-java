package com.tencent.wxcloudrun.entity;

import com.tencent.wxcloudrun.constant.EventEnum;
import java.io.Serializable;
import lombok.Data;

/**
 * @author zhangyichuan 0.001-0.05：微微烧，或者火烧云云况不典型没有预报出来； 0.05~0.2：小烧，大气很通透的情况下才会比较好看；
 *     0.2~0.4：小烧到中等烧； 0.4~0.6：中等烧，比较值得看的火烧云； 0.6~0.8：中等烧到大烧程度的火烧云；
 *     0.8~1.0：不是很完美的大烧火烧云，例如云量没有最高、大气偏污、持续时间偏短、有低云遮挡等； 1.0~1.5：典型的火烧云大烧；
 *     1.5~2.0：优质大烧，火烧云范围广、云量大（不一定满云量）、颜色明亮、持续时间长，且大气通透；
 *     2.0~2.5：世纪大烧，火烧云范围很广、接近满云量、颜色明亮鲜艳、持续时间长，且大气非常通透；
 */
@Data
public class GlowEntity implements Serializable {
  private String summary;

  private String placeHolder;

  private String aod;

  private String eventTime;

  private String quality;

  private String status;

  private EventEnum event;

  public boolean ok() {
    return "ok".equals(status);
  }

  public boolean isNotReady() {
    return "没有该时次的预报".equals(summary);
  }

  public boolean isBeautiful() {
    return getNumQuality() >= 0.4 && getNumAod() < 0.3;
  }

  public boolean isBad() {
    return getNumQuality() < 0.05;
  }

  public String detailStrFormat() {
    if (isBad()) {
      return getEvent().getDesc() + " 不烧\n";
    }
    return getEvent().getDesc()+ "\n"+
            getFormattedEventTime() +
            "\n鲜艳度：" + getStrQuality() +
            "\n污染：" + getStrAod();
  }

  public String getFormattedEventTime() {
    return eventTime.replace("<br>", " ");
  }

  public Double getNumQuality() {
    return Double.parseDouble(quality.substring(0, quality.indexOf("<br>")));
  }

  public String getStrQuality() {
    String num = String.format("%.2f", getNumQuality());
    String str = quality.split("<br>")[1].replace("\n", "");
    if (isBeautiful()) {
      return  num + str +"!!!";
    }
    return num + str;
  }
  
  public Double getNumAod() {
    return Double.parseDouble(aod.substring(0, aod.indexOf("<br>")));
  }
  
  public String getStrAod() {
    return String.format("%.2f", getNumAod()) + aod.split("<br>")[1].replace("\n", "");
  }

  public String getFormattedSummary() {
    String cleanText = summary.replace("&ensp;", "").replace("<b>", "").replace("</b>", "");
    // 按】分割并获取第一部分,加上】
    return cleanText.split("】")[0] + "】";
  }
}
