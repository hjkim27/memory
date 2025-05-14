# nginx
- 가볍고 높은 성능을 가진 웹서버(web Server)
- HTTP Server 로 활용, 정적 파일들을 처리하기 위해 사용됨
- Reverse Proxy Server 로 활용  >> 80 으로 들어오는 내용을 3000, 4000, 5000 등 다른 포트로 분산시킬 수 있음
- 비동기 이벤트 구조를 기반으로 동작

## nginx / apache 차이
서버의 동자 방식 차이

- nginx :: Event-Driven
    - 서버로 들어오는 여러 커넥션을 Event-Handler 를 통해 비동기방식으로 처리하게 함
        \> 적은 메모리로 서버 운영 가능
    - 싱글 스레드, 프로세스로 작동
    - socket read/writer, I/O 등 CPU가 관여하지 않아도 되는 작업을 진행할 때 기다리지 않고 다른 작업 수행
        진행중인 I/O 등 작업이 종료되면 이벤트 발생, 다음 작업 처리 시작

- apache :: Thread/Process
    - 클라이언트 접속시마다 thread 또는 process 생성 처리
        \> 많은 수의 요청 발생 시 속도가 느려짐
    - 다양한 모듈 제공, 확장성이 좋음

### nginx 설치
```bash
# package를 통한 nginx 설치
sudo apt-get install nginx

# version확인
nginx -v
```

### nginx 사용 명령어
```bash
# 시작
sudo systemctl start nginx

# 재시작
sudo systemctl restart nginx

# 중지
sudo systemctl stop nginx

# 상태
sudo systemctl status nginx

# 설정 reload
sudo systemctl reload nginx

# configuration file syntax check
sudo nginx -t
```


## nginx 설정파일 확인 및 옵션 정리
```bash
user      root;
worker_processes                  auto;                                 ## 몇개의 워커 프로세스를 생성할 것인가
pid                               /run/nginx.pid;                       ## nginx 마스터 프로세스 id 정보 저장
include                           /etc/nginx/modules-enabled/*.conf;    ## 서버 속성이 정의되어 있는 파일을 포함시킨다는 명령어 ## nginx 모듈관련:: 동적모듈방식

events {
    worker_connections             768;             ## 하나의 Worker Process 가 동시에 처리할 수 있는 커넥션 수 (default : 1024)
}

http {

    ##
    #                              Basic Settings
    ##
    client_max_body_size           20M;

    sendfile                       on;              ## sendfile() 함수 사용여부 지정
                                                    ### sendfile : 한 파일의 디스크립터와 다른 파일의 디스크립터간에 데이터를 복사하는 것으로 커널 내부에서 복사 진행
    tcp_nopush                     on;
    types_hash_max_size            2048;
    #                              server_tokens off;
    #                              server_names_hash_bucket_size 64;
    #                              server_name_in_redirect off;

    include                        /etc/nginx/mime.types;               ## 포함시킬 외부파일 정의. 파일에 적힌 내용을 현재 파일로 가져옴. # 허용타입
    default_type                   application/octet-stream;            ## 웹서버의 기본 Content-Type 정의

    ##
    #                              SSL Settings
    ##
    ssl_protocols                  TLSv1 TLSv1.1 TLSv1.2 TLSv1.3; # Dropping SSLv3, ref: POODLE
    ssl_prefer_server_ciphers      on;

    ##
    #                              Logging Settings
    ##
    access_log                     /var/log/nginx/access.log;       ## 젒고 로그가 쌓이는 경로
    error_log                      /var/log/nginx/error.log;        ## 에러 로그가 쌓이는 경로

    ##
    #                              Gzip Settings
    ##
    gzip                           on;

    #                              gzip_vary on;
    #                              gzip_proxied any;
    #                              gzip_comp_level 6;
    #                              gzip_buffers 16 8k;
    #                              gzip_http_version 1.1;
    #                              gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;

    ##
    #                              Virtual Host Configs
    ##
    include                        /etc/nginx/conf.d/*.conf;
    include                        /etc/nginx/sites-enabled/*;

    absolute_redirect              off;

    #########################################################################

    ## 백엔드 upstream 설정
    upstream aiseewas {
        server                     127.0.0.1:7070;
        server                     127.0.0.1:7071;  ## weight 2: 가중치 /  backup 쉬다가 쟤 죽으면 얘로
    }

    ## 7070 한번 7071 두번
    # upstream aiseewas {
    #     server                     127.0.0.1:7070;
    #     server                     127.0.0.1:7071 weight 2;  ## weight 2: 가중치 /  backup 쉬다가 쟤 죽으면 얘로
    # }
    
    ## backup 안붙은애만 쓰다가 죽으면 붙은애로
    # upstream aiseewas {
    #     server                     127.0.0.1:7070;
    #     server                     127.0.0.1:7071 backup;
    # }

}
```