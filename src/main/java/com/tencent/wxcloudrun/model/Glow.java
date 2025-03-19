package com.tencent.wxcloudrun.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
// {
// "img_href": "/image/cross_section/深圳_20250319_rise_2025031812z.jpg/",
// "img_summary": "&ensp;\u003Cb\u003E【广东省-深圳】2025-03-19 日出\u003C/b\u003E | 凌晨时次
// (2025031812z) ",
// "place_holder": "深圳",
// "query_id": "7719804",
// "status": "ok",
// "tb_aod": "0.266\u003Cbr\u003E（还不错）",
// "tb_event_time": "2025-03-19\u003Cbr\u003E06:30:09",
// "tb_quality": "0.004\u003Cbr\u003E（微烧）\n"
// }
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

    // 获取格式化后的AOD值
    public String getFormattedAod() {
        return aod.replace("<br>", " ");
    }

    // 获取格式化后的事件时间
    public String getFormattedEventTime() {
        return eventTime.replace("<br>", " ");
    }

    // 获取格式化后的质量
    public String getFormattedQuality() {
        return quality.replace("<br>", " ").replace("\n", "");
    }

    // 获取格式化后的概要,只保留】前的内容
    public String getFormattedSummary() {
        String cleanText = summary.replace("&ensp;", "")
                .replace("<b>", "")
                .replace("</b>", "");
        // 按】分割并获取第一部分,加上】
        return cleanText.split("】")[0] + "】";
    }

    public String format() {
        return getFormattedEventTime() + " 概率: " + getFormattedQuality();
    }
}
