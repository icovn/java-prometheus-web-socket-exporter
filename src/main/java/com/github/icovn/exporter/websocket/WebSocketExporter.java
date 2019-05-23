package com.github.icovn.exporter.websocket;

import com.github.strengthened.prometheus.healthchecks.HealthChecksCollector;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.MetricsServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class WebSocketExporter {

  private final HealthChecksCollector healthChecksCollector;
  private final int openPort;

  public WebSocketExporter(HealthChecksCollector healthChecksCollector, int openPort) {
    this.healthChecksCollector = healthChecksCollector;
    this.openPort = openPort;
  }

  public void export() throws Exception {
    CollectorRegistry.defaultRegistry.register(this.healthChecksCollector);

    Server server = new Server(openPort);
    ServletContextHandler context = new ServletContextHandler();
    context.setContextPath("/");
    server.setHandler(context);

    context.addServlet(new ServletHolder(new MetricsServlet()), "/metrics");

    server.start();
    server.join();
  }
}
