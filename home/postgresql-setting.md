# postgresql
## 0. 설치
- `apt-get` 사용
```bash
apt-get inatall postgresql

# 특정버전을 사용하고자 할 경우 버전 명시
```
## 1. 기본설정
### 원격접속 허용
```bash
# vi /etc/postgresql/16/main/postgresql.conf
listen_address="*"
```
```bash
# /etc/postgresql/16/main/pg_hba.conf
# Database administrative login by Unix domain socket
local   all             postgres                                peer

# TYPE  DATABASE        USER            ADDRESS                 METHOD
# "local" is for Unix domain socket connections only
#local   all             all                                     peer
local   all             all                                     scram-sha-256
# IPv4 local connections:
host    all             all             127.0.0.1/32            scram-sha-256
# IPv6 local connections:
host    all             all             ::1/128                 scram-sha-256
host    all             all             192.168.45.0/24         scram-sha-256 # 동일 게이트웨이 접속허용
# Allow replication connections from localhost, by a user with the
# replication privilege.
local   replication     all                                     peer
host    replication     all             127.0.0.1/32            scram-sha-256
host    replication     all             ::1/128                 scram-sha-256
```
- 설정변경 후 `systemctl reload postgresql`

### `md5` vs `scram-sha-256`
| 항목               | `md5`                 | `scram-sha-256`                    |
| ---------------- | --------------------- | ---------------------------------- |
| 보안 수준         | 낮음 (구버전 알고리즘)         | 높음 (현대적인 안전한 방식)                   |
| 해시 알고리즘       | MD5                   | SHA-256 + Salt + Iteration         |
| 암호 저장         | 해시만 저장                | `salted hash + iteration count` 저장 |
| 통신 중 암호화      | 해시 전송 (패스워드 노출 위험 있음) | 안전한 챌린지-응답 방식                      |
| PostgreSQL 지원 | 예전부터 지원               | PostgreSQL 10 이상                   |
| 클라이언트 지원      | 대부분 지원                | 최신 클라이언트 필요 (ex: psycopg2 최신버전 등)  |

#### md5 방식
- 암호화된 비밀번호를 서버에 저장: md5(md5(password + username))
- 로그인 시 클라이언트가 그 해시를 보내고 서버가 비교
- 취약점: 동일한 비밀번호에 대해 해시값이 항상 동일하므로 사전공격(dictionary attack), replay 공격에 약함
#### scram-sha-256 방식
- 비밀번호 해시 + salt + iteration count를 사용해 안전하게 저장
- 로그인 시에는 챌린지-응답 방식으로 통신하여 비밀번호 자체나 해시가 직접 전송되지 않음
- PostgreSQL 10 이상에서 기본 지원하며, 표준에 기반한 안전한 인증 방식

### DB 생성
```sql
CREATE DATABASE dbname
  WITH 
    TEMPLATE template0  -- 기본 템플릿DB대신 최소 설정인 template0 사용 >> locale 설정 가능
    ENCODING 'UTF8'     -- 문자 인코딩 방식
    LC_COLLATE='C'      -- 문자열 정렬 방식 : 빠름 but 언어정렬이 아님
    LC_CTYPE='C'        -- 문자 분류 방식 : 문자의 대소문자 등 처리방식
    OWNER your_user;    -- DB소유자 (미지정 시 현재 접속한 사용자)
```

## 2. 계정설정
### 1. 계정추가
```sql
-- 계정 추가
CREATE USER newuser WITH PASSWORD 'secure_password';

-- 비밀번호 변경
ALTER USER newuser WITH PASSWORD 'secure_password_new';
```
### 2. 권한 
```sql
-- DB 생성권한 부여
ALTER USER newuser CREATEDB;
```
#### (1) 특정 DB에 대한 권한 부여
```sql
-- 권한 부여 대상 DB접속
psql -U postgres -d target_database

-- crud 부여 : 현재 생성되어있는 테이블 대상
GRANT CONNECT ON DATABASE your_database TO newuser;
GRANT USAGE ON SCHEMA public TO newuser;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO newuser;

-- 이후 생성될 테이블에도 자동으로 권한부여
ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO newuser;
```

#### (2) 권한 제거
```SQL
-- DB 생성권한 제거
ALTER USER username NOCREATEDB;

```
- 권한 부여에서 `gront ~ on  ~ to` 를 `revoke ~on ~ from` 으로 수정
```sql
-- 권한 제거 대상 DB접속
psql -U postgres -d target_database

-- crud 제거 : 현재 생성되어있는 테이블 대상
REVOKE CONNECT ON DATABASE dbname FROM username;
REVOKE USAGE ON SCHEMA public FROM username;
REVOKE SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public FROM username;

-- 이후 생성될 테이블에도 자동으로 부여한 권한 제거
ALTER DEFAULT PRIVILEGES IN SCHEMA public
REVOKE SELECT, INSERT, UPDATE, DELETE ON TABLES FROM username;
```

#### (3) role  수정
```sql
-- 슈퍼유저 권한 부여 : postgres 내 모든 작업 무제한 수행 가능
alter
role root superuser;  

-- 다른 계정정을 생성/수정/삭제할 권한 부여
alter
role root createrole;

--  데이터베이스 생성 권한 부여
alter
role root createdb;
```

### 3. 권한 확인
```postg
\du     -- 사용자 목록 및 권한
\l      -- 데이터베이스 목록
\dp     -- 테이블 권한 보기
```

### 4. `grant` `alter role` 차이점

| 구분               | `ALTER ROLE`                    | `GRANT`                           |
|------------------|---------------------------------|-----------------------------------|
| **무엇에 대한 권한인가?** | PostgreSQL **시스템 수준 권한**        | **데이터베이스 객체(테이블, 스키마 등) 권한**      |
| **대상**           | 역할(사용자)의 **역할 속성** 변경           | **특정 객체에 대한 접근 권한** 부여            |
| **예**            | SUPERUSER, CREATEDB, CREATEROLE | SELECT, INSERT, UPDATE, CONNECT 등 |

- `alter role`: 사용자의 역할/자격을 설정
- `grant`: 사용자의 객체에 대한 사용 권한 설정