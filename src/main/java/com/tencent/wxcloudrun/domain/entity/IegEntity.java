package com.tencent.wxcloudrun.domain.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * @author zhangyichuan
 * @date 2025/5/22
 */
@Data
public class IegEntity implements Serializable {
  private String receiver;

  private String content;
}
