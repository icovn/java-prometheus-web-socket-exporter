package com.github.icovn.exporter.websocket.model;

import java.util.List;
import lombok.Data;

@Data
public class SocketConfiguration {

  private int port;

  private List<SocketRequest> requests;
}
