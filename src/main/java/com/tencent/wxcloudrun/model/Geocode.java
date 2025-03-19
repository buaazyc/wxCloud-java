package com.tencent.wxcloudrun.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Geocode {
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("geocodes")
    private InnerGeocode[] geocodes;

    public class InnerGeocode {
        @JsonProperty("city")
        private String city;
        @JsonProperty("location")
        private String location;
    }
}
