# loadaverage 관련 확인
- vm 에서 테스트 진행
- 2.102 (에이전트 : 1)에서 확인했을  경우에도 중간중간 cpu사용률이 10% 로 치솟는 현상이 확인됨
- vmstat 부하확인
```
procs -----------memory---------- ---swap-- -----io---- -system-- ------cpu-----
r  b   swpd   free   buff  cache   si   so    bi    bo   in   cs us sy id wa st
0  0  66560 110708  25716 1190436    0    0     4     0    0  353  1  0 99  0  0
0  0  66560 110708  25716 1190436    0    0     0     0    2  254  0  0 100  0  0
1  0  66560 110708  25716 1190440    0    0     0     8    0  433 21  9 70  0  0
0  0  66560 110708  25716 1190440    0    0     0     8    2  433 24  6 70  0  0
0  0  66560 110708  25716 1190444    0    0     0     0    0  233  0  0 100  0  0
0  0  66560 109936  25724 1190436    0    0     0    24    2  273  0  1 99  0  0
0  0  66560 109952  25724 1190444    0    0     0     0    0  139  0  0 100  0  0
2  0  66560 109952  25724 1190444    0    0     0     0    2  181  0  0 100  0  0
2  0  66560 109952  25724 1190444    0    0     0     0    0  176  0  0 100  0  0
0  0  66560 109952  25724 1190444    0    0     0     0    2  170  1  0 99  0  0
2  0  66560 109952  25724 1190444    0    0     0     0    0  182  0  0 100  0  0
2  0  66560 109952  25724 1190444    0    0     0   136    2  234  0  0 100  0  0
0  0  66560 109952  25724 1190444    0    0     0     0    0  181  0  0 100  0  0
2  0  66560 109952  25724 1190444    0    0     0     0    2  474 45 11 43  0  0
0  0  66560 109952  25724 1190444    0    0     0    32    0  570  4  0 95  1  0
0  0  66560 109952  25724 1190452    0    0     0     0    2  192  1  0 99  0  0
```
> r : 현재 실행되고 있는 프로세스 개수      
> b: I/O를 위해 대기열에 있는 프로세스 개수     

- R이 많을 경우
    1) cpu, 메모리 사용량 이외에는 정상인 경우 
        -  프로그램 로직, 알고리즘/하드웨어 개선 필요
    2) 특정 프로그램의 cpu점유율이 높은 경우 
        -  오류 제거 ,프로그램이 정상적으로 동작하도록 수정 필요
- D가 많을 경우
    1) 프로그램으로부터 입출력이 많아 부하가 높은 경우  
        -  파일 입출력 부분 개선 필요
    2) swapping이 발생>디스크 액세스가 발생하고있는 경우 
        -  특정 프로세스가 극단적으로 메모리를 소비하고 있지는 않은지 ps 확인       
            프로그램 오류로 메모리를 지나치게 사용하고 있는 경우엔 프로글매 개선        
            또는 물리적인 램 증설
