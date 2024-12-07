# centOS 및 2074 에서 2.0.10 이 배포가 안되는 현상

## 기존 버전 확인
209 미만 버전으로 installer가 실행된 서버의 경우 java, tomcat 버전 업그레이드가 필요

## 20200325_V2.0.7.1(release) 기준 버전
```
java 	: 1.7.0_79
tomcat 	: 7.0.63
psql 	: 9.4.9 
```


## jdk-8u191-ojdkbuild-linux-x64.tar.gz, tomcat.tar.gz 파일 옮긴 후 실행
### 1) jdk ----------
```
tar xvf jdk-8u191-ojdkbuild-linux-x64.tar.gz
mv jdk-8u191-ojdkbuild-linux-x64 /usr/local/jdk-8u191
update-alternatives --install /usr/bin/java   java  /usr/local/jdk-8u191/bin/java  1
update-alternatives --install /usr/bin/javac  javac /usr/local/jdk-8u191/bin/javac 1
update-alternatives --install /usr/bin/jps    jps   /usr/local/jdk-8u191/bin/jps   1
```

#### /usr/local/shpc/etc/shpc_profile 값 변경
```bash
JAVA_HOME=/usr/local/jdk-8u191
#JAVA_HOME=$SHPC/jdk1.7
```

### 2) tomcat ----------
#### 기존 tomcat은 shutdown.sh 실행 후 작업 진행
```
mv /usr/local/shpc/tomcat /usr/local/shpc/tomcat7
tar -zxvf tomcat.tar.gz
mv tomcat /usr/local/shpc/
cd /usr/local/shpc/tomcat/bin
chmod 744 *.sh
```

### 3) jdk, tomcat 버전 변경 후 접속중이던 putty 접속해제 후 다시 붙어주세요.
** tomcat startup.sh 실행 시 jdk가 정상적으로 변경되었을 경우 아래 JRE_HOME 으로 확인됩니다.
```
Using JRE_HOME:        /usr/local/jdk-8u191"" 
```

```
tar xvf jdk-8u191-ojdkbuild-linux-x64.tar.gz
mv jdk-8u191-ojdkbuild-linux-x64 ./lib

alternatives --install ~/bin/java java ~/lib/jdk-8u191/bin/java 1
alternatives --install ~/bin/javac javac ~/lib/jdk-8u191/bin/javac 1
alternatives --install ~/bin/jar jar ~/lib/jdk-8u191/bin/jar 1
alternatives --set java ~/lib/jdk-8u191/bin/java
alternatives --set javac ~/lib/jdk-8u191/bin/javac
alternatives --set jar ~/lib/jdk-8u191/bin/jar

alternatives --config java
```