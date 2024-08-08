## restTeamplate 
타임아웃제한이 없어 응답이 오지 않을 경우 대기상태에 빠짐    
모든 쓰레드가 대기상태에 빠지면 다른 요청을 응답할 스레드가 남아있지 않게 됨

1. 타임아웃 설정 필요 ======
- RestTemplateBuilder 를 사용한 방식이 많음.
- 현재 netCenter 에서는 RestTemplateBuilder 가 선언되지 않음 (버전문제인듯)
   >> 다른 어디서 오류가 발생할지 모르므로 최소한만 변경해 HttpComponentsClientHttpRequestFactory 를 사용하기로 함

```java
// 기존
private static RestTemplate restTemplate = new RestTemplate();

// 수정
public static RestTemplate restTemplate(){
    HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
    factory.setConnectTimeout(10*1000);
    factory.setReadTimeout(10*1000);
    RestTemplate restTemplate = new RestTemplate(factory);
    return restTemplate;
}
```
