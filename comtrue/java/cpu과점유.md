`top -c` 로 cpu 확인 시    
`-Djava.util.logging.config.file=/usr/local/shpc/tomcat/conf/logging.properties` 가 cpu를 과점유하고있음.    

약 80% 를 java가 먹고있는 현상

---------------------------------------------------------------------------------------------------------------------

- `df -h` 를 실행할 때 오류가 발생하면 프로세스가 종료되지 않아 문제가 발생하는것으로 보임
- 해당 프로세스를 강제종료하면 20%대로 낮아진다고 함.
- 일반 다른 서버에서 확인했을 때 java가 10~20% 정도를 점유하고 있어 정상범위로 판단

#### df -h 를 실행하는 곳에서 일정 시간이 지나도 정상 응답이 없을 경우 해당 프로세스를 종료하는 로직
```java
Process process = Runtime.getRuntime().exec("df -h");
if(!process.waitFor(5, TimeUnit.SECONDS)){
    process.destroy();
    log.warn("df -h Timeout Occurred.");
    return fileSystemInfoList;
}
```
