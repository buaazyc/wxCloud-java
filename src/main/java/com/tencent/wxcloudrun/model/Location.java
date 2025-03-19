package com.tencent.wxcloudrun.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class Location implements Serializable {
    private Integer status; // 值为0或1,0表示失败；1表示成功
    private String info; // 状态信息,成功为"OK"
    private Integer infocode; // 状态码：https://lbs.amap.com/api/webservice/guide/tools/info

    private String country;
    private String province;
    private String city;
    private String district;

    private String location;
}
