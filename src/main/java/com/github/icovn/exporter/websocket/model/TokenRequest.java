package com.github.icovn.exporter.websocket.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor(staticName = "of")
@Data
public class TokenRequest {

  private String refreshToken;
}
