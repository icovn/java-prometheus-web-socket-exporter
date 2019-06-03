package com.github.icovn.exporter.websocket.client;

import com.github.icovn.exporter.websocket.constant.TokenConstant;
import eu.mivrenik.stomp.StompHeader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.drafts.Draft;

@Slf4j
public class MyOauthStompClient extends MyStompClient {

  private String id;

  public MyOauthStompClient(URI serverUri, Draft protocolDraft,
      Map<String, String> httpHeaders, int connectTimeout, boolean enableHeader) {
    super(serverUri, protocolDraft, httpHeaders, connectTimeout, enableHeader);
  }

  public MyOauthStompClient(URI serverUri, Draft protocolDraft,
      Map<String, String> httpHeaders, int connectTimeout, boolean enableHeader, String id) {
    super(serverUri, protocolDraft, httpHeaders, connectTimeout, enableHeader);
    this.id = id;
  }

  protected Map<String, String> buildHeaders(){
    log.info("(buildHeaders)");
    Map<String, String> requestHeaders = new HashMap<>();
    requestHeaders.put(StompHeader.ACCEPT_VERSION.toString(), STOMP_VERSION);
    if(enableHeader){
      requestHeaders.put(StompHeader.HOST.toString(), uri.getHost());
    }
    for(Map.Entry<String, String> entry: httpHeaders.entrySet()){
      if(entry.getKey().equals("authorization")){
        String token = TokenStorage.getInstance().get(TokenConstant.ACCESS_TOKEN + id);
        log.info("(buildHeaders)stored token: {}", token);
        if(token != null){
          requestHeaders.put(entry.getKey(), "Bearer " + token);
        }else {
          log.info("(buildHeaders)config token: {}", entry.getValue());
          requestHeaders.put(entry.getKey(), entry.getValue());
        }
      }else {
        requestHeaders.put(entry.getKey(), entry.getValue());
      }
    }

    return requestHeaders;
  }
}
