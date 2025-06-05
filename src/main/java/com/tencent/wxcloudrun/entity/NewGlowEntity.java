package com.tencent.wxcloudrun.entity;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangyichuan
 * @date 2025/6/5
 */
@Data
public class NewGlowEntity {

    private String address;

    private List<SingleGlowEntity> glows;

    @Data
    public static class SingleGlowEntity {

        /**
         * 今天、明天、后天
         */
        private String date;

        /**
         * 早上，晚上
         */
        private String amPm;

        private Double quality;

        private Integer qualityLevel;

        private Double aod;

        private Integer aodLevel;

        public boolean isBad() {
            return qualityLevel < 2;
        }

        public String format() {
            String res = "";
            if(isBad()) {
                res = date + "-" + amPm + " 不烧";
            } else {
                res = date + "-" + amPm +
                        "\n鲜艳度：" + quality +
                        "\n等级：" + qualityLevel +"【"+ qualityLevelFormat(qualityLevel) + "】";
            }
            return res + "\n---------------------------";
        }
    }

    private static Map<Integer, String> qualityLevelMap = new HashMap<Integer, String>() {{
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
    }};

    private static String qualityLevelFormat(Integer qualityLevel) {
        return qualityLevelMap.get(qualityLevel);
    }

    public String format() {
        StringBuilder res = new StringBuilder("【"+address+"】火烧云预测：");
        for (SingleGlowEntity glow : glows) {
            res.append("\n").append(glow.format());
        }
        return res.toString();
    }
}
