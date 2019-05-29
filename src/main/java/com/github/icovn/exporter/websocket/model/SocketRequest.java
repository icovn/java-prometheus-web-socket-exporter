package com.github.icovn.exporter.websocket.model;

import java.util.List;
import lombok.Data;

@Data
public class SocketRequest {

  private String url;

  private List<SocketHeader> headers;

  private Boolean isStomp;


  private Boolean isOauth = false;

  private String id;

  private String cron;

  private String refreshToken;

  private String refreshUrl;
}
