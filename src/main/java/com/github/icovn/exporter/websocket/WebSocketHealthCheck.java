package com.github.icovn.exporter.websocket;

import com.github.icovn.exporter.websocket.client.MyOauthStompClient;
import com.github.icovn.exporter.websocket.client.MyStompClient;
import com.github.icovn.exporter.websocket.client.MyWebSocketClient;
import com.github.icovn.exporter.websocket.model.SocketHeader;
import com.github.icovn.exporter.websocket.model.SocketRequest;
import com.github.icovn.util.ExceptionUtil;
import com.github.strengthened.prometheus.healthchecks.HealthCheck;
import com.github.strengthened.prometheus.healthchecks.HealthStatus;
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
    try {
      URI uri = new URI(request.getUrl());
      if (request.getIsStomp()) {
        Map<String, String> headers = new HashMap<>();
        for (SocketHeader header : request.getHeaders()) {
          headers.put(header.getKey(), header.getValue());
        }
        log.info("(checkWebSocket)headers: {}", headers);

        MyStompClient client = new MyStompClient(uri, new Draft_6455(), headers, 5000);
        if(request.getIsOauth()){
          client = new MyOauthStompClient(uri, new Draft_6455(), headers, 5000, false, request.getId());
        }
        log.info("(checkWebSocket)start connect");
        client.connectBlocking();

        while (client.isConnecting()){
          log.info("(checkWebSocket)connecting ...");
          Thread.sleep(1000);
        }
        log.info("(checkWebSocket)connect state: {}", client.isStompConnected());

        if (client.isStompConnected()) {
          client.close();
          return true;
        } else {
          client.close();
          return false;
        }
      } else {
        MyWebSocketClient myWebSocketClient = new MyWebSocketClient(uri);
        myWebSocketClient.connectBlocking();
        myWebSocketClient.close();
      }
    } catch (Exception ex) {
      log.error(
          "(checkWebSocket)uri: {}, ex: {}",
          request.getUrl(),
          ExceptionUtil.getFullStackTrace(ex, true));
    }

    return true;
  }
}
