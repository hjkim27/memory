# jeus 설치 --------------------

3. 환경설정
```bash
    # vi ~/.bash_profile ** 맨 아래 추가
    export JAVA_HOME=/usr/jdk/jdk-8u191
    export PATH=$JAVA_HOME/bin:$PATH
    export JEUS_HOME=/home/jeus
```

4. jeus 설치    https://velog.io/@chrisantus/Linux-JEUS-8-%EC%84%A4%EC%B9%98
    - license파일 교체
    ** license 발급 https://technet.tmaxsoft.com/ko/front/main/main.do
        - 발급유형 Demo
        - Host Name :: 서버 hostname 확인 후 적을 것


5. jeus 실행    https://blog.naver.com/tawoo0/221559022526
```bash
    # DAS(Domain Admin Server) 시작
        startDomainAdminServer -domain jeus_domain -u admin -p privacy
    # nodeManager 시작
        nohup startNodeManager > /home/jeus/nodemanager/logs/nm.log &
    # MS(Managed Server) 시작/중지
        startManagedServer -dasurl localhost:9736 -domain jeus_domain -server server1 -u admin -p privacy
```

6. 방화벽설정
    방법1)
        firewall-cmd --permanent --zone=public --add-port=80/tcp
        firewall-cmd --reload
    방법2) 그냥 해제
        systemctl stop firewalld


6. 관리페이지 접속
    http://[서버 IP]:9736/webadmin
    ```
    # 로그인계정 : <id> / <password>    // id(default): administrator
    ```

    ** jeus 서버 추가 및 배포방법   https://hanjonghyuk.tistory.com/13

