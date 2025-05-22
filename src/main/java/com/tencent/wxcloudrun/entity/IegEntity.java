package com.tencent.wxcloudrun.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyichuan
 * @date 2025/5/22
 */
@Data
public class IegEntity implements Serializable {
    private String receiver;

    private String content;
}
