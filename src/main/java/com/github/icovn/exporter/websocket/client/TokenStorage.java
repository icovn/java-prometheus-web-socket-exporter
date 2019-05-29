package com.github.icovn.exporter.websocket.client;

import java.util.HashMap;
import java.util.Map;

public class TokenStorage {

  private Map<String, String> storage;

  private static TokenStorage ourInstance = new TokenStorage();

  public static TokenStorage getInstance() {
    return ourInstance;
  }

  private TokenStorage() {
    storage = new HashMap<>();
  }

  public String get(String key){
    return storage.get(key);
  }

  public void set(String key, String value){
    storage.put(key, value);
  }
}
