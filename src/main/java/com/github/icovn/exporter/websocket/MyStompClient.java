package com.github.icovn.exporter.websocket;

import com.github.icovn.util.ExceptionUtil;
import eu.mivrenik.stomp.StompCommand;
import eu.mivrenik.stomp.StompFrame;
import eu.mivrenik.stomp.StompHeader;
import eu.mivrenik.stomp.client.StompClient;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

@Slf4j
public class MyStompClient extends StompClient {

  private boolean isConnecting;

  protected final boolean enableHeader;
  protected final Map<String, String> httpHeaders;

  public MyStompClient(
      URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders, int connectTimeout) {
    this(serverUri, protocolDraft, httpHeaders, connectTimeout, false);
  }

  public MyStompClient(
      URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders, int connectTimeout,
      boolean enableHeader) {
    super(serverUri, protocolDraft, httpHeaders, connectTimeout);
    this.httpHeaders = httpHeaders;
    this.enableHeader = enableHeader;
  }

  public boolean isConnecting() {
    return isConnecting;
  }

  @Override
  public void onOpen(ServerHandshake handshakedata) {
    log.info("(onOpen)");
    isConnecting = true;
    connectStomp();
  }

  @Override
  public void onMessage(String message) {
    log.info("(onMessage)message: {}", message);
    if(message != null && message.startsWith("CONNECTED")){
      isConnecting = false;
      super.onMessage(message);
    }
  }

  @Override
  public void onClose(int code, String reason, boolean remote) {
    log.info("(onClose)code: {}, reason: {}, remote: {}", code, reason, remote);
    isConnecting = false;
    super.onClose(code, reason, remote);
  }

  @Override
  public void onError(Exception ex) {
    log.info("(onError)ex: {}", ExceptionUtil.getFullStackTrace(ex, true));
  }

  @Override
  protected void connectStomp() {
    log.info("(connectStomp)");
    if (stompConnected) {
      return;
    }
    if (stompConnectionListener != null) {
      stompConnectionListener.onConnecting();
    }

    Map<String, String> requestHeaders = buildHeaders();

    send(new StompFrame(StompCommand.CONNECT, requestHeaders).toString());
  }

  @Override
  public void send(String text) {
    log.info("(send)text: {}", text);
    super.send(text);
  }

  protected Map<String, String> buildHeaders(){
    Map<String, String> requestHeaders = new HashMap<>();
    requestHeaders.put(StompHeader.ACCEPT_VERSION.toString(), STOMP_VERSION);
    if(enableHeader){
      requestHeaders.put(StompHeader.HOST.toString(), uri.getHost());
    }
    for(Map.Entry<String, String> entry: httpHeaders.entrySet()){
      requestHeaders.put(entry.getKey(), entry.getValue());
    }

    return requestHeaders;
  }
}
