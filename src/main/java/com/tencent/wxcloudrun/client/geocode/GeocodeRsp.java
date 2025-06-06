package com.tencent.wxcloudrun.client.geocode;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

/**
 * @author zhangyichuan
 * @date 2025/6/5
 */
@Data
public class GeocodeRsp {

    /**
     * 状态 返回值为 0 或 1，0 表示请求失败；1 表示请求成功。
     */
    @JsonProperty("status")
    private String status;

    /**
     * 返回结果的个数。
     */
    @JsonProperty("count")
    private String count;

    /**
     * 返回结果说明,当 status 为 0 时，info 会返回具体错误原因，否则返回“OK”。
     */
    @JsonProperty("info")
    private String info;

    /**
     * 结果列表。
     */
    @JsonProperty("geocodes")
    private List<Geocode> geocodes;

    @Data
    public static class Geocode {

        /**
         * 地址信息
         */
        @JsonProperty("formatted_address")
        private String formattedAddress;

        /**
         * 地址信息。经纬度，用逗号分隔
         */
        @JsonProperty("location")
        private String location;
    }

    public boolean ok() {
        return "1".equals(status) && !geocodes.isEmpty();
    }

    public String getLocation() {
        return geocodes.get(0).getLocation();
    }

    public String getFormattedAddress() {
        return geocodes.get(0).getFormattedAddress();
    }
}
