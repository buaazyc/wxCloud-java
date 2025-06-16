package com.tencent.wxcloudrun.domain.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;

@Component
public class ResourceMonitor {

  private static double maxCpuUsage = 0.0;
  private static double maxMemoryUsage = 0.0;

  public static void run() {
    SystemInfo systemInfo = new SystemInfo();
    HardwareAbstractionLayer hardware = systemInfo.getHardware();

    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // 每秒记录一次 CPU 和内存使用情况
    scheduler.scheduleAtFixedRate(
        () -> {
          double cpuUsage = getCpuUsage(hardware.getProcessor());
          double memoryUsage = getMemoryUsage(hardware.getMemory());

          // 更新最大值
          if (cpuUsage > maxCpuUsage) {
            maxCpuUsage = cpuUsage;
          }
          if (memoryUsage > maxMemoryUsage) {
            maxMemoryUsage = memoryUsage;
          }
        },
        0,
        1,
        TimeUnit.SECONDS);

    // 一分钟后停止任务并输出最大值
    scheduler.schedule(
        () -> {
          System.out.print("过去一分钟内的峰值：\n");
          System.out.printf("最高 CPU 使用率: %.2f%%\n", maxCpuUsage);
          System.out.printf("最高内存使用率: %.2f%%\n", maxMemoryUsage);
          scheduler.shutdownNow(); // 停止调度器
        },
        60,
        TimeUnit.SECONDS);
  }

  /** 获取当前 CPU 使用率 */
  private static double getCpuUsage(CentralProcessor processor) {
    long[] prevTicks = processor.getSystemCpuLoadTicks();
    try {
      Thread.sleep(500); // 等待一段时间以计算负载变化
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    long[] ticks = processor.getSystemCpuLoadTicks();

    long user =
        ticks[CentralProcessor.TickType.USER.getIndex()]
            - prevTicks[CentralProcessor.TickType.USER.getIndex()];
    long nice =
        ticks[CentralProcessor.TickType.NICE.getIndex()]
            - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
    long sys =
        ticks[CentralProcessor.TickType.SYSTEM.getIndex()]
            - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
    long idle =
        ticks[CentralProcessor.TickType.IDLE.getIndex()]
            - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
    long iowait =
        ticks[CentralProcessor.TickType.IOWAIT.getIndex()]
            - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
    long irq =
        ticks[CentralProcessor.TickType.IRQ.getIndex()]
            - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
    long softIrq =
        ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()]
            - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
    long steal =
        ticks[CentralProcessor.TickType.STEAL.getIndex()]
            - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];

    long totalCpu = user + nice + sys + idle + iowait + irq + softIrq + steal;
    if (totalCpu == 0) return 0;

    return 100.0 * (user + nice + sys) / totalCpu;
  }

  /** 获取当前内存使用率 */
  private static double getMemoryUsage(GlobalMemory memory) {
    long totalMemory = memory.getTotal();
    long freeMemory = memory.getAvailable();
    long usedMemory = totalMemory - freeMemory;
    return 100.0 * usedMemory / totalMemory;
  }
}
