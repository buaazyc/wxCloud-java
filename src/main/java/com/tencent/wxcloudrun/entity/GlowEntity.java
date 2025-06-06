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

    private Double quality;

    private Integer qualityLevel;

    private Double aod;

    private Integer aodLevel;

    public boolean isBad() {
      return qualityLevel < 1;
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
                + "\n鲜艳度："
                + quality
                + "\n等级："
                + qualityLevel
                + "【"
                + qualityLevelFormat(qualityLevel)
                + "】";
      }
      return res + "\n---------------------------";
    }
  }

  private static Map<Integer, String> qualityLevelMap =
      new HashMap<Integer, String>() {
        {
          put(0, "不烧");
          put(1, "微微烧");
          put(2, "小烧");
          put(3, "小烧到中烧");
          put(4, "中烧");
          put(5, "中烧到大烧");
          put(6, "大烧");
          put(7, "优质大烧");
          put(8, "超级大烧");
          put(9, "世纪大烧");
        }
      };

  private static String qualityLevelFormat(Integer qualityLevel) {
    return qualityLevelMap.get(qualityLevel);
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
