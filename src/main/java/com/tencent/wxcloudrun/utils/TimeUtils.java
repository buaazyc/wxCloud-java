package com.tencent.wxcloudrun.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * @author zhangyichuan
 * @date 2025/6/5
 */
public class TimeUtils {

  /** 获取今天的日期（格式：yyyyMMdd） */
  public static String getDay(long addDays) {
    LocalDate today = LocalDate.now().plusDays(addDays);
    return today.format(DateTimeFormatter.BASIC_ISO_DATE);
  }

  public static ZonedDateTime today() {
    return LocalDateTime.now()
        .atZone(ZoneId.systemDefault())
        .withZoneSameInstant(ZoneId.of("Asia/Shanghai"));
  }

  public static ZonedDateTime parseDateTime(String dateTime) {
    return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        .atZone(ZoneId.of("Asia/Shanghai"));
  }

  public static long getDaysBetween(ZonedDateTime start, ZonedDateTime end) {
    LocalDate startDate = start.toLocalDate();
    LocalDate endDate = end.toLocalDate();
    return Period.between(startDate, endDate).getDays();
  }

  public static String parseDay(long days) {
    if (days == 0) {
      return "今天";
    }
    if (days == 1) {
      return "明天";
    }
    if (days == 2) {
      return "后天";
    }
    if (days == 3) {
      return "大后天";
    }
    return "";
  }

  public static String getAmPm(ZonedDateTime dateTime) {
    return dateTime.getHour() < 12 ? "上午" : "下午";
  }
}
