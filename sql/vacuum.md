psql  쿼리 업데이트 진행 중 /var/lib/postgresql 폴더의 크기가 점점 늘어나는 현상 발견
- 현재 206G > 287G 로 증가한 것으로 확인되며, 계속 증가할 것으로 보임

- 실행한 쿼리 : update_V2.0.9.0.sql    
    - alter table, create table, create trigger 등의 쿼리 다수 존재
- 실행중인 쿼리 : alter table pcscan_current_detail_result add column document_classification integer not null default 0
----------------------------------------------------------------------------------------------------------------
psql 테이블 부풀림 현상으로 예상됨 (참고 : https://postgresql.kr/blog/postgresql_table_bloating.html )    
> 다수의 테이블을 대상으로 update, delete, alter, trigger 를 진행하고 있어 테이블 부풀림 현상이 발생한 것으로 보임

#### 테이블 부풀림 현상
PostgreSQL에서 해당 테이블 대상으로 UPDATE, DELETE 명령을 이용해서 자료 조작이 일어난다면, 그 테이블 크기는 실 자료 크기보다 커질 수 밖에 없음.    
사용자의 자료 조작 뿐만 아니라, 트리거나, 함수를 통해서 일어나는 자료 변경, 삭제도 동일

#### 2. 테이블 부풀림현상 확인 쿼리
- vacuum 을 통해 테이블을 정리할 필요가 있음.
```sql
select relname, n_dead_tup from pg_stat_user_tables;     --  vacuum 이 필요한 확인      
SELECT pg_size_pretty(pg_database_size('mydb'))          --  데이터베이스 용량 확인
SELECT pg_size_pretty(pg_relation_size('mytable'))       --  테이블 사이즈 확인         
SELECT pg_size_pretty(pg_index_size('mytable'))          --  인덱스 크기를 계산
SELECT pg_size_pretty(pg_total_relation_size('mytable')) --  테이블 사이즈 체크 시 연관 인덱스 사이즈 합산 
```

#### 3. vacuum 실행 명령어 ** full 옵션은 사용 시 데이터베이스가 잠금처리됨. 운영 시 문제가 발생할 수 있음. 
```sql
-- DB 전체 풀 실행                        vacuum full analyze;
-- DB 전체 간단하게 실행              vacuum verbose analyze;
-- 해당 테이블만 간단하게 실행     vacuum analyse [테이블 명];
-- 특정 테이블만 풀 실행              vacuum full [테이블명];
```
** verbose 옵션을 사용할 경우 n_dead_tup 은 줄어들지만 테이블 자체 용량은 줄어들지 않음    
- full 옵션을 사용할 경우 테이블과 DB 자체 용량을 줄일 수 있지만 DB 가 잠기기 때문에 문제가 생길 수 있음    
- autovacuum 설정은 옵션없는 표준 vacuum 과 동일함.    
