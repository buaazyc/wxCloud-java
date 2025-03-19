package com.tencent.wxcloudrun.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Geocode implements Serializable {
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("geocodes")
    private InnerGeocode[] geocodes;

    @Data
    public static class InnerGeocode {
        @JsonProperty("city")
        private String city;
        @JsonProperty("location")
        private String location;
    }

    public String getLocation() {
        if (null == geocodes || geocodes.length == 0)
            return null;
        return geocodes[0].getLocation();
    }

    public double[] getDoubleLocation() {
        String[] latlng = getLocation().split(",");
        return new double[] { Double.parseDouble(latlng[0]), Double.parseDouble(latlng[1]) };
    }

}
