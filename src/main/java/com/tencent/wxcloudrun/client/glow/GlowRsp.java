package com.tencent.wxcloudrun.client.glow;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.wxcloudrun.entity.GlowEntity;
import com.tencent.wxcloudrun.time.TimeUtils;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * @author zhangyichuan
 * @date 2025/6/5
 */
@Data
public class GlowRsp {

    @JsonProperty("status")
    private int status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("version")
    private String version;

    @JsonProperty("date")
    private DateInfo date;

    @JsonProperty("result")
    private ResultInfo result;

    @Data
    public static class DateInfo {

        @JsonProperty("time")
        private String time;

        @JsonProperty("timeZone")
        private String timeZone;
    }

    @Data
    public static class ResultInfo {

        @JsonProperty("start")
        private String start;

        @JsonProperty("end")
        private String end;

        @JsonProperty("size")
        private int size;

        @JsonProperty("meteCodes")
        private List<String> meteCodes;

        @JsonProperty("datas")
        private List<DataItem> dataList;
    }

    @Data
    public static class DataItem {

        @JsonProperty("fc_time")
        private String fcTime;

        @JsonProperty("values")
        private List<Double> values;

        @JsonProperty("levels")
        private List<Integer> levels;
    }

    public boolean ok() {
        return status == 0;
    }

    public GlowEntity rspToEntity() {
        GlowEntity res = new GlowEntity();
        res.setGlows(new ArrayList<>());
        ZonedDateTime today = TimeUtils.today();
        for (DataItem dataItem : result.getDataList()) {
            GlowEntity.SingleGlowEntity entity = new GlowEntity.SingleGlowEntity();
            ZonedDateTime dateTime = TimeUtils.parseDateTime(dataItem.getFcTime());
            entity.setDate(TimeUtils.parseDay(TimeUtils.getDaysBetween(today, dateTime)));
            entity.setAmPm(TimeUtils.getAmPm(dateTime));

            entity.setAod(dataItem.getValues().get(0));
            entity.setAodLevel(dataItem.getLevels().get(0));

            entity.setQuality(dataItem.getValues().get(1));
            entity.setQualityLevel(dataItem.getLevels().get(1));

            res.getGlows().add(entity);
        }
        return res;
    }

}
