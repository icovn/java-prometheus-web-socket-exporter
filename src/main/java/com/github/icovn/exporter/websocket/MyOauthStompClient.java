package com.github.icovn.exporter.websocket;

import eu.mivrenik.stomp.StompHeader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.drafts.Draft;

@Slf4j
public class MyOauthStompClient extends MyStompClient {

  public MyOauthStompClient(URI serverUri, Draft protocolDraft,
      Map<String, String> httpHeaders, int connectTimeout, boolean enableHeader) {
    super(serverUri, protocolDraft, httpHeaders, connectTimeout, enableHeader);
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
