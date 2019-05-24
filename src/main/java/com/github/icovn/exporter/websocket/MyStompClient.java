package com.github.icovn.exporter.websocket;

import com.github.icovn.util.ExceptionUtil;
import eu.mivrenik.stomp.StompFrame;
import eu.mivrenik.stomp.StompHeader;
import eu.mivrenik.stomp.client.StompClient;
import java.net.URI;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

@Slf4j
public class MyStompClient extends StompClient {

  public MyStompClient(URI serverUri, Draft protocolDraft,
      Map<String, String> httpHeaders, int connectTimeout) {
    super(serverUri, protocolDraft, httpHeaders, connectTimeout);
  }

  @Override
  public void onOpen(ServerHandshake handshakedata) {
    log.info("(onOpen)");
    connectStomp();
  }

  @Override
  public void onMessage(String message) {
    log.info("(onMessage)message: {}", message);
    super.onMessage(message);
  }

  @Override
  public void onClose(int code, String reason, boolean remote) {
    log.info("(onClose)code: {}, reason: {}, remote: {}", code, reason, remote);
    super.onClose(code, reason, remote);
  }

  @Override
  public void onError(Exception ex) {
    log.info("(onError)ex: {}", ExceptionUtil.getFullStackTrace(ex, true));
  }

}
