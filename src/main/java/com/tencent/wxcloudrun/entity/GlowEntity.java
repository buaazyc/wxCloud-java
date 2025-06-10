package com.tencent.wxcloudrun.entity;

import com.tencent.wxcloudrun.client.solar.SolarRsp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhangyichuan
 * @date 2025/6/5
 */
@Data
@Slf4j
public class GlowEntity {

  private String address;

  private List<SingleGlowEntity> glows;

  @Data
  public static class SingleGlowEntity {
    private String date;

    /** 今天、明天、后天 */
    private String dateName;

    /** 上午，下午 */
    private String amPmName;

    // 等级说明：https://datacloud.geovisearth.com/support/meteorological/flameCloudForecast
    private Double quality;

    private Integer qualityLevel;

    private Double aod;

    private Integer aodLevel;

    /** 日出日落时间 */
    private String sunTime;

    public boolean isBad() {
      return qualityLevel <= 0;
    }

    public boolean isNoData() {
      return quality > 999;
    }

    public boolean isBeautiful() {
      return qualityLevel >= 3;
    }

    public boolean isArriving() {
      return "今天".equals(dateName) || ("明天".equals(dateName) && "上午".equals(amPmName));
    }

    public String format() {
      String res = "";
      if (isBad()) {
        res = dateName + "-" + amPmName + " 不烧";
      } else {
        res =
            dateName
                + "-"
                + amPmName
                + " "
                + sunTime
                + "\n火烧云质量："
                + quality
                + "【"
                + qualityLevelFormat(qualityLevel)
                + "】"
                + "\n空气污染: "
                + aod
                + "【"
                + aodLevelFormat(aodLevel)
                + "】";
      }
      return res + "\n---------------------------";
    }
  }

  private static Map<Integer, String> qualityLevelMap =
      new HashMap<Integer, String>() {
        {
          put(0, "无");
          put(1, "微烧");
          put(2, "小烧");
          put(3, "中烧");
          put(4, "大烧");
          put(5, "世纪大烧");
        }
      };

  private static String qualityLevelFormat(Integer qualityLevel) {
    String res = qualityLevelMap.get(qualityLevel);
    if (res == null) {
      log.error("qualityLevel not found, qualityLevel = {}", qualityLevel);
      return "世纪大烧";
    }
    return res;
  }

  private static Map<Integer, String> aodLevelMap =
      new HashMap<Integer, String>() {
        {
          put(1, "重度雾霾");
          put(2, "中度雾霾");
          put(3, "轻度雾霾");
          put(4, "普通");
          put(5, "通透");
          put(6, "水晶天");
          put(7, "高级水晶天");
        }
      };

  private static String aodLevelFormat(Integer aodLevel) {
    String res = aodLevelMap.get(aodLevel);
    if (res == null) {
      log.error("aodLevel not found, aodLevel = {}", aodLevel);
      return "高级水晶天";
    }
    return res;
  }

  public String messageFormat() {
    StringBuilder res = new StringBuilder("【" + address + "】火烧云预测");
    for (SingleGlowEntity glow : glows) {
      res.append("\n").append(glow.format());
    }
    return res.toString();
  }

  public String emailFormatWithFilter() {
    StringBuilder res = new StringBuilder();
    for (SingleGlowEntity glow : glows) {
      if (glow.isBeautiful() && glow.isArriving()) {
        res.append("\n").append(glow.format());
      }
    }
    if ("".contentEquals(res)) {
      return "";
    }
    return "【" + address + "】：\n" + res;
  }

  public void setSunTime(SolarRsp solar) {
    for (SingleGlowEntity glow : glows) {
      for (SolarRsp.DataItem dataItem : solar.getResult().getDataList()) {
        if (glow.getDate().equals(dataItem.getDate())) {
          if ("上午".equals(glow.getAmPmName())) {
            glow.setSunTime(dataItem.getSunrise());
          } else {
            glow.setSunTime(dataItem.getSunset());
          }
        }
      }
    }
  }

  public boolean isNoData() {
    for (SingleGlowEntity glow : glows) {
      if (glow.isNoData()) {
        log.error("glow is no data, address = {}, rsp = {}", address, glow);
        return true;
      }
    }
    return false;
  }
}
