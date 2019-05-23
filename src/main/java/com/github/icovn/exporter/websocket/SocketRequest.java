package com.github.icovn.exporter.websocket;

import java.util.List;
import lombok.Data;

@Data
public class SocketRequest {

  private String url;

  private List<String> headers;
}
