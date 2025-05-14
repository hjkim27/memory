## websphere 설치
### 서버구축
#### 1. webSphere 파일 다운로드
- https://developer.ibm.com/wasdev/downloads/#asset/runtimes-wlp-webProfile8
- 동일하게 9.0.5.12 버전을 다운받아 테스트 하려 했으나, 이전 버전 다운로드가 불가능해 최신버전으로 다운로드 진행.
- `\\192.168.2.13\dev\WEB_DEV\was\webSphere` 

#### 2. 설치
- `/usr/local/webSphere/wlp` 경로로 설정되도록 설치 진행함
- 반드시 java linux 버전을 다운로드해야함. linux 버전이 없을 경우 배포가 잘 되지 않음.
> yum install unzip     
> cd /usr/local/webSphere       
> unzip wlp-webProfile8-java8-linux-x86_64-24.0.0.8.zip        

#### 3. 구성
- `/usr/local/webSphere/wlp/bin` 하위에서 실행
```bash
# 서버 생성
./installUtility install server1      
./server create server1

# 서버 시작/종료 :: 4. war 배포설정의 server.xml 설정 후 서버 시작
./server start/stop server1

# 웹 관리 콘솔(liberty 관리센터) 설치 :: 선택
# ./installUtility install adminCenter-1.0       

# java ee 8 지원을 위한 full 설치 :: 선택
# ./installUtility install javaee-8.0        
```

#### 4. war 배포 설정
- `/usr/local/webSphere/wlp/usr/servers/server1/apps` 하위에 admin.war 넣기.
- server.xml 수정

```xml
<?xml version="1.0" encoding="UTF-8"?>
<server description="new server">

    <!-- Enable features -->
    <featureManager>
        <feature>webProfile-8.0</feature>
    </featureManager>

    <!-- 
        To access this server from a remote client add a host attribute to the following element, 
        e.g. host="*" 
    -->
    <httpEndpoint id="defaultHttpEndpoint"
                host="*"
                httpPort="7070"
                httpsPort="9443" />

    <!-- Automatically expand WAR files and EAR files -->
    <applicationManager autoExpand="true"/>

    <!-- location에 절대경로가 아니라 war 파일명만 적을 경우 해당 서버의 apps 하위의 war 를 검색한다. -->
    <webApplication contextRoot="admin" location="admin.war" />
</server>
```
- 참고URL : https://gomban.tistory.com/24

#### 5. 방화벽 설정
- centos/redhat 의 경우 방화벽 설정 필요
```bash
firewall-cmd --zone=public --permanent --add-port=7070/tcp
firewall-cmd --reload
```
- 참고URL : https://velog.io/@duck-ach/Linux-RedHat-8-CentOS-%EB%B0%A9%ED%99%94%EB%B2%BD-%ED%8F%AC%ED%8A%B8-%EB%B2%88%ED%98%B8-%EC%97%B4%EA%B8%B0
