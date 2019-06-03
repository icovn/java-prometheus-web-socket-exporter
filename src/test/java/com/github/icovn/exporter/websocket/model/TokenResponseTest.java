package com.github.icovn.exporter.websocket.model;

import static org.junit.Assert.*;

import com.github.icovn.util.ExceptionUtil;
import com.github.icovn.util.MapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class TokenResponseTest {

  @Test
  public void fromJson() {
    String json =
        "{\"userId\":\"99fc32de-056d-44c5-8347-ddacf87d197b\",\"name\":null,\"username\":\"gvtinh1\",\"firstName\":\"gv\",\"lastName\":\"M?t\",\"middleName\":\"T?nh\",\"birthday\":1558458000000,\"email\":\"hoangtinh101001@gmail.com\",\"phone\":\"376-0987654324333\",\"role\":null,\"timeZone\":\"Asia/Ho_Chi_Minh\",\"profilePhoto\":null,\"accessToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJndnRpbmgxIiwidHlwZSI6IkFDQ0VTU19UT0tFTiIsImlzcyI6Imh0dHA6Ly9raWR0b3BpLmNvbSIsImp0aSI6Ijk5ZmMzMmRlLTA1NmQtNDRjNS04MzQ3LWRkYWNmODdkMTk3YiIsImlhdCI6MTU1OTU4MTc0MCwiZXhwIjoxNTU5NzYxNzQwfQ.0V5TNiw1HVmv8b2Uywa-kBpHt_9hStZ8NttdEpFVZ5O0T45k51Jhq05Zvhoh5ANRpRlt-rNXJEthA2enYkfXrQ\",\"refreshToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJndnRpbmgxIiwidHlwZSI6IlJFRlJFU0hfVE9LRU4iLCJpc3MiOiJodHRwOi8va2lkdG9waS5jb20iLCJqdGkiOiI5OWZjMzJkZS0wNTZkLTQ0YzUtODM0Ny1kZGFjZjg3ZDE5N2IiLCJpYXQiOjE1NTk1ODE3NDAsImV4cCI6MTU1OTk0MTc0MH0.egIQYx3vXFrIzsx-8T6N62YBRQOUiD49zMheAG8ZV-V-lIt1K9J5DA8FR1YW7pRHCNBdtxBZZSE2fiQ94D82ng\",\"expiredTime\":180000,\"uploadToken\":\"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5OWZjMzJkZS0wNTZkLTQ0YzUtODM0Ny1kZGFjZjg3ZDE5N2IiLCJBcHAgTmFtZSI6ImtpZHRvcGlfRklMRSIsImlzcyI6Imh0dHA6Ly9raWR0b3BpLmNvbSIsImp0aSI6Ijk5ZmMzMmRlLTA1NmQtNDRjNS04MzQ3LWRkYWNmODdkMTk3YiIsImlhdCI6MTU1OTU4MTc0MCwiZXhwIjoxNTU5NzYxNzQwfQ.yHMrQJhvcrbSos3NwepCChl_UUYY9HwfmcUAL9GMcteucVM_vU1SynFiRMT6hdR71z5VHNU3AaskZyDDHjMfIg\",\"timeZoneOffset\":\"+07:00\"}";
    try{
      TokenResponse tokenResponse = MapperUtil.getMapper().readValue(json, TokenResponse.class);
      log.info("(fromJson)tokenResponse: {}", tokenResponse);
      assertNotNull(tokenResponse);
    }catch (Exception ex){
      log.error("(fromJson)ex: {}", ExceptionUtil.getFullStackTrace(ex));
    }

  }
}