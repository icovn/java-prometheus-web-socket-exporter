package com.github.icovn.exporter.websocket;

import java.net.URI;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

@Slf4j
public class MyWebSocketClient extends WebSocketClient {

  public MyWebSocketClient(URI serverUri,
      Map<String, String> httpHeaders) {
    super(serverUri, httpHeaders);
  }

  @Override
  public void onOpen(ServerHandshake serverHandshake) {
    log.info("(onOpen)open: {}", this.uri);
  }

  @Override
  public void onMessage(String s) {
    log.info("(onMessage)message: {}", s);
  }

  @Override
  public void onClose(int i, String s, boolean b) {
    log.info("(onClose)message: {}", s);
  }

  @Override
  public void onError(Exception e) {
    log.info("(onError)ex: {}", e.getMessage());
  }
}
