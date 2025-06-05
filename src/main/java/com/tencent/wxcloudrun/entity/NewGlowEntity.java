package com.tencent.wxcloudrun.entity;

import lombok.Data;

import java.util.List;

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
                        "\n等级：" + qualityLevel +
                        "\n污染等级：" + aodLevel;
            }
            return res + "\n---------------------------";
        }
    }

    public String format() {
        StringBuilder res = new StringBuilder("【"+address+"】火烧云预测：");
        for (SingleGlowEntity glow : glows) {
            res.append("\n").append(glow.format());
        }
        return res.toString();
    }
}
