package com.tencent.wxcloudrun.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Glow implements Serializable {
    private Integer status;
    private String version;
    private DateInfo date;
    private Result result;

    @Data
    public static class DateInfo {
        private String time;
        private String timeZone;
    }

    @Data
    public static class Result {
        private String start;
        private String end;
        private Integer size;
        @JsonProperty("meteCodes")
        private List<String> meteCodes;
        private List<ForecastData> datas;
    }

    @Data
    public static class ForecastData {
        @JsonProperty("fc_time")
        private String fcTime;
        private List<Double> values;
        private List<Integer> levels;
    }
}
