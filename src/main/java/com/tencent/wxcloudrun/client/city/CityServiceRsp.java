package com.tencent.wxcloudrun.client.city;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author zhangyichuan
 * @date 2025/5/23
 */
@Data
public class CityServiceRsp{
    @JsonProperty("city_list")
    private String[] cityList;
}
