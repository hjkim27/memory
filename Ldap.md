 AD연동 관련 정리    
- 검색조건, 각 입력 필드가 어떻게 사용되는지 확인    
** adsync 에서 입력하는 query는 사용자 검색 쿼리가 아닌 특정 테이블을 칭함.     
** 해당 테이블에서 검색을 위한 쿼리는 하드코딩되어있음.     
  > 일반적인 검색조건으로 하드코딩되어있어 해당 고객사에서 어떤 식으로 테이블을 관리했는지 확인 필요    
  > 현재 검색 쿼리는 사용자일 경우 조회가 되도록 되어있음.
  > 
** 해당 AD 에서 objectClass와 objectCategory 확인 후 쿼리 재작성 필요. > 쿼리 수정 후 동작 확인 예정

https://docs.google.com/spreadsheets/d/1YrN15kHnMnXKVX4y2k5G4IT3aHddNk4y3IN6KdXtxQY/edit?usp=sharing

