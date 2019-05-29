package com.github.icovn.exporter.websocket;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import com.github.icovn.exporter.websocket.client.TokenStorage;
import com.github.icovn.exporter.websocket.constant.TokenConstant;
import com.github.icovn.exporter.websocket.model.SocketConfiguration;
import com.github.icovn.exporter.websocket.model.SocketRequest;
import com.github.icovn.exporter.websocket.schedule.RefreshTokenJob;
import com.github.icovn.util.ExceptionUtil;
import com.github.strengthened.prometheus.healthchecks.HealthChecksCollector;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

@Slf4j
public class Application {

  public static void main(String[] args) throws Exception {
    log.info("(main)args: {}", args);
    if (args == null || args.length < 1) {
      throw new RuntimeException("Please set configuration file path when run");
    }

    SocketConfiguration configuration = readConfig(args[0]);
    if (configuration == null) {
      throw new RuntimeException("Configuration file not found or invalid, file path: " + args[1]);
    }

    HealthChecksCollector healthChecksMetrics =
        HealthChecksCollector.Builder.of()
            .setGaugeMetricName("web_socket_health_check")
            .setGaugeMetricHelp("Web socket health check.")
            .build();

    for (SocketRequest request : configuration.getRequests()) {
      healthChecksMetrics.addHealthCheck(request.getUrl(), new WebSocketHealthCheck(request));
    }

    initSchedule(configuration);
    new WebSocketExporter(healthChecksMetrics, configuration.getPort()).export();
  }

  private static void initSchedule(SocketConfiguration configuration){
    log.info("(initSchedule)");
    try {
      // Grab the Scheduler instance from the Factory
      Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

      for(SocketRequest request: configuration.getRequests()){
        if(request.getIsOauth()){
          JobDataMap dataMap = new JobDataMap();
          dataMap.put(TokenConstant.ID.name(), request.getId());
          dataMap.put(TokenConstant.REFRESH_TOKEN.name(), request.getRefreshToken());
          dataMap.put(TokenConstant.REFRESH_URL.name(), request.getRefreshUrl());

          // define the job and tie it to our HelloJob class
          JobDetail job = newJob(RefreshTokenJob.class)
              .withIdentity("job" + request.getId(), "group" + request.getId())
              .usingJobData(dataMap)
              .build();

          // Trigger the job to run
          CronTrigger trigger = newTrigger()
              .withIdentity("trigger" + request.getId(), "group" + request.getId())
              .withSchedule(CronScheduleBuilder.cronSchedule(request.getCron()))
              .forJob(job)
              .build();

          // Tell quartz to schedule the job using our trigger
          scheduler.scheduleJob(job, trigger);
        }
      }

      // and start it off
      scheduler.start();
    } catch (SchedulerException ex) {
      log.error("(initSchedule)ex: {}", ExceptionUtil.getFullStackTrace(ex, true));
    }
  }

  private static SocketConfiguration readConfig(String path) {
    try {
      Yaml yaml = new Yaml(new Constructor(SocketConfiguration.class));
      InputStream inputStream = new FileInputStream(path);

      return yaml.load(inputStream);
    } catch (FileNotFoundException ex) {
      log.error("(readConfig)ex: {}", ex.getMessage());
      return null;
    }
  }
}
