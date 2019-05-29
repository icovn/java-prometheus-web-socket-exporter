package com.github.icovn.exporter.websocket.model;

import lombok.Data;

@Data
public class TokenResponse {

  private String accessToken;

  private String refreshToken;
}
