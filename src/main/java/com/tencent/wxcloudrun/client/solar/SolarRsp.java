package com.tencent.wxcloudrun.client.solar;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

/**
 * @author zhangyichuan
 * @date 2025/6/7
 */
@Data
public class SolarRsp {

  @JsonProperty("code")
  private int code;

  @JsonProperty("msg")
  private String msg;

  @JsonProperty("status")
  private int status;

  @JsonProperty("version")
  private String version;

  @JsonProperty("result")
  private ResultInfo result;

  @Data
  public static class ResultInfo {
    @JsonProperty("datas")
    private List<DataItem> dataList;
  }

  @Data
  public static class DataItem {
    @JsonProperty("date")
    private String date;

    @JsonProperty("sunrise")
    private String sunrise;

    @JsonProperty("sunset")
    private String sunset;
  }
}
