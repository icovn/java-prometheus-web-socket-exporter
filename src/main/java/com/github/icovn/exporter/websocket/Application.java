package com.github.icovn.exporter.websocket;

import com.github.strengthened.prometheus.healthchecks.HealthChecksCollector;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

@Slf4j
public class Application {

  public static void main( String[] args ) throws Exception {
    log.info("(main)args: {}", args);
    if(args == null || args.length < 1){
      throw new RuntimeException("Please set configuration file path when run");
    }

    SocketConfiguration configuration = readConfig(args[0]);
    if(configuration == null){
      throw new RuntimeException("Configuration file not found or invalid, file path: " + args[1]);
    }

    HealthChecksCollector healthChecksMetrics = HealthChecksCollector.Builder.of()
        .setGaugeMetricName("web_socket_health_check")
        .setGaugeMetricHelp("Web socket health check.")
        .build();
    healthChecksMetrics.addHealthCheck("state", new WebSocketHealthCheck(configuration.getRequests()));

    new WebSocketExporter(healthChecksMetrics, configuration.getPort()).export();
  }

  private static SocketConfiguration readConfig(String path) {
    try{
      Yaml yaml = new Yaml(new Constructor(SocketConfiguration.class));
      InputStream inputStream = new FileInputStream(path);
      SocketConfiguration configuration = yaml.load(inputStream);
      return configuration;
    }catch (FileNotFoundException ex){
      log.error("(readConfig)ex: {}", ex.getMessage());
      return null;
    }
  }
}
