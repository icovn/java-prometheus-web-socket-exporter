package com.github.icovn.exporter.websocket;

import com.github.icovn.util.ExceptionUtil;
import com.github.strengthened.prometheus.healthchecks.HealthCheck;
import com.github.strengthened.prometheus.healthchecks.HealthStatus;
import eu.mivrenik.stomp.client.StompClient;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.drafts.Draft_6455;

@Slf4j
public class WebSocketHealthCheck extends HealthCheck {

  private final SocketRequest request;

  public WebSocketHealthCheck(SocketRequest request) {
    this.request = request;
  }

  @Override
  protected HealthStatus check() throws Exception {
    return checkWebSocket() ? HealthStatus.HEALTHY : HealthStatus.UNHEALTHY;
  }

  private boolean checkWebSocket() {
    log.info("(checkWebSocket)request: {}", request);
    try{
      URI uri = new URI(request.getUrl());
      if(request.getIsStomp()){
        Map<String, String> headers = new HashMap<>();
        for(SocketHeader header: request.getHeaders()){
          headers.put(header.getKey(), header.getValue());
        }

        StompClient client = new StompClient(uri, new Draft_6455(), headers, 5000);
        log.info("(checkWebSocket)start connect");
        client.connectBlocking();
        log.info("(checkWebSocket)connect state: {}", client.isStompConnected());

        if(client.isStompConnected()){
          client.subscribe("/topic/greetings", message -> {
            log.info("(checkWebSocket)server message: " + message);
            // Disconnect
            client.close();
          });
        }else {
          return false;
        }
        log.info("(checkWebSocket)end connect");
      } else {
        MyWebSocketClient myWebSocketClient = new MyWebSocketClient(uri);
        myWebSocketClient.connectBlocking();
        myWebSocketClient.close();
      }
    } catch (Exception ex) {
      log.error("(checkWebSocket)uri: {}, ex: {}", request.getUrl(), ExceptionUtil.getFullStackTrace(ex, true));
    }

    return true;
  }
}
