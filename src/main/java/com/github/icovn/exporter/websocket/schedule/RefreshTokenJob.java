package com.github.icovn.exporter.websocket.schedule;

import com.github.icovn.exporter.websocket.client.TokenStorage;
import com.github.icovn.exporter.websocket.constant.TokenConstant;
import com.github.icovn.exporter.websocket.model.TokenRequest;
import com.github.icovn.exporter.websocket.model.TokenResponse;
import com.github.icovn.util.ExceptionUtil;
import com.github.icovn.util.MapperUtil;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Slf4j
public class RefreshTokenJob implements Job {

  private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

  private OkHttpClient client = new OkHttpClient();

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
    log.info("(execute)dataMap: {}", dataMap);
    String id = dataMap.getString(TokenConstant.ID.name());
    String refreshToken = dataMap.getString(TokenConstant.REFRESH_TOKEN.name());
    String refreshUrl = dataMap.getString(TokenConstant.REFRESH_URL.name());
    log.info("(execute)refresh token: {}", refreshToken);
    log.info("(execute)refresh url: {}", refreshUrl);

    TokenResponse tokenResponse = refresh(refreshUrl, refreshToken);
    log.info("(execute)tokenResponse: {}", tokenResponse);
    if(tokenResponse != null){
      log.info("(execute)update token, tokenResponse: {}", tokenResponse);
      TokenStorage.getInstance().set(TokenConstant.ACCESS_TOKEN + id, tokenResponse.getAccessToken());

      dataMap.put(TokenConstant.REFRESH_TOKEN.name(), tokenResponse.getRefreshToken());
    }
  }

  private TokenResponse refresh(String url, String token) {
    log.info("(refresh)url: {}, token: {}", url, token);
    RequestBody body = RequestBody.create(JSON, MapperUtil.toJson(TokenRequest.of(token)));
    Request request = new Request.Builder()
        .url(url)
        .post(body)
        .build();
    try (Response response = client.newCall(request).execute()) {
      String responseBody = response.body().string();
      log.info("(refresh)http code: {}, body: {}", response.code(), responseBody);
      return MapperUtil.getMapper().readValue(responseBody, TokenResponse.class);
    } catch (Exception ex){
      log.error("(refresh)ex: {}", ExceptionUtil.getFullStackTrace(ex, true));
      return null;
    }
  }
}
