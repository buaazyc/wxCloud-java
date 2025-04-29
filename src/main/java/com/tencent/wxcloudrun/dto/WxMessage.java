package com.tencent.wxcloudrun.dto;

import lombok.Data;

/**
 * @author zhangyichuan
 */
@Data
public class WxMessage {
  private CmdType cmdType;
  private String[] tokens;

  public WxMessage(String content) {
    if (content == null) {
      return;
    }
    String trimmed = content.trim();
    if (trimmed.isEmpty()) {
      return;
    }
    tokens = trimmed.split("\\s+");
    cmdType = CmdType.Default;
  }
}

enum CmdType {
  Default;
}
