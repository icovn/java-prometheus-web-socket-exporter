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

    for(SocketRequest request: configuration.getRequests()){
      healthChecksMetrics.addHealthCheck(request.getUrl(), new WebSocketHealthCheck(request));
    }

    new WebSocketExporter(healthChecksMetrics, configuration.getPort()).export();
  }

  private static SocketConfiguration readConfig(String path) {
    try{
      Yaml yaml = new Yaml(new Constructor(SocketConfiguration.class));
      InputStream inputStream = new FileInputStream(path);

      return yaml.load(inputStream);
    } catch (FileNotFoundException ex){
      log.error("(readConfig)ex: {}", ex.getMessage());
      return null;
    }
  }
}
