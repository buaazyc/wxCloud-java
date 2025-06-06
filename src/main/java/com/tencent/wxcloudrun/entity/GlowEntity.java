package com.tencent.wxcloudrun.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * @author zhangyichuan
 * @date 2025/6/5
 */
@Data
public class GlowEntity {

  private String address;

  private List<SingleGlowEntity> glows;

  @Data
  public static class SingleGlowEntity {

    /** 今天、明天、后天 */
    private String date;

    /** 早上，晚上 */
    private String amPm;

    // 等级说明：https://datacloud.geovisearth.com/support/meteorological/flameCloudForecast
    private Double quality;

    private Integer qualityLevel;

    private Double aod;

    private Integer aodLevel;

    public boolean isBad() {
      return qualityLevel <= 0;
    }

    public boolean isBeautiful() {
      return qualityLevel >= 4;
    }

    public boolean isArriving() {
      return "今天".equals(date) || ("明天".equals(date) && "早上".equals(amPm));
    }

    public String format() {
      String res = "";
      if (isBad()) {
        res = date + "-" + amPm + " 不烧";
      } else {
        res =
            date
                + "-"
                + amPm
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
    return qualityLevelMap.get(qualityLevel);
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
    return aodLevelMap.get(aodLevel);
  }

  public String messageFormat() {
    StringBuilder res = new StringBuilder("【" + address + "】火烧云预测：");
    for (SingleGlowEntity glow : glows) {
      res.append("\n").append(glow.format());
    }
    return res.toString();
  }

  public String emailFormatWithFilter() {
    StringBuilder res = new StringBuilder();
    for (SingleGlowEntity glow : glows) {
      if (glow.isBeautiful() && glow.isArriving()) {
        res.append(glow.format());
      }
    }
    if ("".contentEquals(res)) {
      return "";
    }
    return "【" + address + "】：\n" + res;
  }
}
