package com.github.icovn.exporter.websocket;

import com.github.strengthened.prometheus.healthchecks.HealthCheck;
import com.github.strengthened.prometheus.healthchecks.HealthStatus;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebSocketHealthCheck extends HealthCheck {

  private final List<SocketRequest> requests;

  public WebSocketHealthCheck(
      List<SocketRequest> requests) {
    this.requests = requests;
  }

  @Override
  protected HealthStatus check() throws Exception {
    return checkWebSocket() ? HealthStatus.HEALTHY : HealthStatus.UNHEALTHY;
  }

  private boolean checkWebSocket() {
    log.info("(checkWebSocket)requests: {}", requests);
    for(SocketRequest request: requests){
      try{
        URI uri = new URI(request.getUrl());
        Map<String, String> headers = new HashMap<>();
        MyWebSocketClient myWebSocketClient = new MyWebSocketClient(uri, headers);
        myWebSocketClient.connectBlocking();
        myWebSocketClient.send("CONNECT");
        myWebSocketClient.close();
      }catch (URISyntaxException ex){
        log.error("(checkWebSocket)uri: {}, ex: {}", request.getUrl(), ex.getMessage());
        return false;
      } catch (InterruptedException ex) {
        log.error("(checkWebSocket)uri: {}, ex: {}", request.getUrl(), ex.getMessage());
        return false;
      }
    }

    return true;
  }
}
