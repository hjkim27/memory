1. api 호출방식 
    - 로그인페이지가 존재하는 앞단에서 작업, 사용하는 메서드
    - 각 관리자가 로그인을 시도할 경우 이후 모든 페이지는 해당 메서드를 통해 다른 서버의 정보를 가져온다.
    - 서버의 정보(port, host)는 db에 저장하며, admin_info 의 값이 변경될 경우 개별DB에 반영 및 thread 를 사용해 주기적으로 반영하도록 추가한다.
    - admin_info 에는 서버정보(port, host) 외에도 DB정보(port, host) :: 레디스 저장용 을 추가한다.

#### 호출메서드
```java
public static JsonObject getJsonData(String url, String params, HttpServletRequest request) {
    Map<String, Object> map = LoginUtil.getAccessInfo(request);
    String serverHost = String.valueOf(map.get(LoginUtil.SERVER_HOST_NAME)); // 사용자정보에 저장해서 갖고있는다.
    String serverPort = String.valueOf(map.get(LoginUtil.SERVER_PORT_NAME));

    url = "http://"+serverHost+":"+serverPort+url;
    
    String returnResult = "";
    JsonObject obj = null;
    int timeout = 10;
    RequestConfig reqConf = RequestConfig.custom()
                                  .setConnectTimeout(timeout*3000)
                                  .setConnectionRequestTimeout(timeout*3000)
                                  .setSocketTimeout(timeout*3000)
                            .build();
    
    CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(reqConf).build();
    CloseableHttpResponse httpResponse = null;
    InputStreamReader isr = null;
    BufferedReader br = null;
    try {
        HttpPost httpPost = new HttpPost(url);
        EntityBuilder entity = EntityBuilder.create();
        
        log.info(entity.toString());
        log.info(url);
        entity.setText(params);
        
        httpPost.addHeader("content-type", "application/json;charset=UTF-8");
        httpPost.setEntity(entity.build());

        httpResponse = httpClient.execute(httpPost);

        isr = new InputStreamReader(httpResponse.getEntity().getContent());
        br = new BufferedReader(isr);
        
        String line = "";
        while((line = br.readLine()) != null) {
            returnResult += line+"\n";
        }
        httpPost.releaseConnection();
        
    } catch (UnsupportedEncodingException e) {
        log.error(e.getMessage(), e);
    } catch (ClientProtocolException e) {
        log.error(e.getMessage(), e);
    } catch (IOException e) {
        log.error(e.getMessage(), e);
    } finally {
        try {
            if(br != null) br.close();
            if(isr != null) isr.close();
            if(httpResponse != null) httpResponse.close();
            if(httpClient != null) httpClient.close();
        } catch (Exception e2) {
            log.error(e2.getMessage(), e2);
        }
    }
    obj = (JsonObject) JsonParser.parseString(returnResult);
    return obj;
}
```

#### 예시 (앞단)

1. api를 사용한 값 조회
```java
JsonObject json = DataApi.getJsonData("/report/titleCheck.ctr", new Gson().toJson(map), req);
```

2. List<Object> 형식의 값 추출

```java
List<ReportBean> reportList = obMapper.readValue(json.get("reportList").toString(), new TypeReference<List<ReportBean>>(){});
```

3. Object 형식의 값 추출
```java
ReportSearch search = obMapper.readValue(json.get("search").toString(), ReportSearch.class);
```

4. 일반적인 형식의 값 추출
```java
int count = json.get("count").getAsInt();
String message = json.get("message").getAsString();
```


#### 예시 (뒷단)

- @ResponseBody 를 사용해 return 값 형식 확인
- @RequestBody 를 사용해 자동으로 객체에 값을 매핑해사용한다. (안 될 경우도 발생할 수 있으니 반드시 확인 필요)
```java
@RequestMapping(value="/management/UserList.ctr")    
public @ResponseBody Map<String, Object> list(HttpServletRequest request, HttpServletResponse response, @RequestBody int id) throws Exception {

    String action = "시스템관리>사용자 정보";
    String comment = "열람";
   
    adminLogService.insertLogs(request, action, comment);
    Map<String, Object> mov = new HashMap<String, Object>();
    
    mov.put("admin", adminInfoService.getUser(id));
    mov.put("license", boardWebserverIpService.getWebServerGroupByDomain().size());        
    return mov;
}
```

** Bean 객체에서 변수로 선언하지 않고 메서드로만 사용하던 값이 존재할 경우 오류가 발생할 수 있음

ex1) admin_info
  - api 단에서 아래와 같이 변수로 선언되지 않고 특정 값을 계산해서 return 하는 메서드의 경우 오류 발생
  - 앞단에서만 해당 메서드를 넣으면서 오류 해결 (추가확인 필요)
```java
public Boolean isPasswdExpired() {
    Date dd = passwdModifiedTime;
    Long expire = dd.getTime() / ( 24*60*60*1000L ) + ( GeneralConfig.PASSWD_EXPIRE_PERIOD);
    Long now = Calendar.getInstance().getTimeInMillis() / ( 24*60*60*1000L );
    if ( expire > now ) {
        return false;
    }
    return true;
}
```

ex2) DefaultSearch
  - api 단에서 아래와 같이 변수로 선언되지 않고 특정 값을 계산해서 return 하는 메서드의 경우 오류 발생
  - ex1) 과 같이 해당 메서드를 삭제, 해결하려 했으나 오류가 해결되지 않음.
  - 앞단에 계산결과값과 동일한 변수명을 추가한 후 정상동작 확인 완료



==========

2. api 호출방식 관련 확인

1) UserListController  : 관리자정보 출력방식 변경으로 대부분의 메서드를 사용하지 않게 됨.

-- 사용

   @RequestMapping(value="/management/UserList.ctr") :: 사용자정보 페이지로 변경

-- 미사용

   @RequestMapping(value="/management/UserDelete.ctr") :: 관리자 삭제 메서드

   @RequestMapping(value="/management/AccessIpDelete.ctr") :: 관리자접속 IP삭제

   @RequestMapping(value="/management/UserExcelDownload.ctr") :: 관리자목록 엑셀 저장 페이지

   @RequestMapping(value="/management/UserRegExcel.ctr", method = {RequestMethod.GET, RequestMethod.POST}) :: 관리자목록 엑셀저장

   @RequestMapping(value="/management/UserRegExcelProcess.ctr") :: 관리자IP목록 엑셀저장

   @RequestMapping(value="/management/resetLoginBlockCount.ctr") :: 로그인차단횟수 초기화

   @RequestMapping(value="/management/modifyPassword.ctr") :: 비밀번호 변경

   @RequestMapping(value="/management/editPasswordSetting.ctr") :: 비밀번호변경설정 수정


2) 사용하지 않는 메서드만 포함한 controller

- ScanController :; 스캔탭 관련

- ServerFormController, ServerListController :: 사용하지 않는 것으로 확인

- SystemInfoController :: 기존 관리자정보 탭을 사용할 경우 해당 페이지는 보여지지 않게 조건처리되어있음. 사용하지 않는 것으로 확인

- UserFormController :: 사용자정보 중 비밀번호만 수정이 가능하게 수정되었고, 해당 컨트롤러에서는 작업하지 않음. 사용X

