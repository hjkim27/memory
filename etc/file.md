### 1. 기존 저장된 사진은 되는데 새로 촬영한 사진은 안됨 > 용량이 큰 파일이 안되는 거였음

- maxPostSize = -1 세팅
   - spring.servlet.multipart.max-file-size=200MB
   - spring.servlet.multipart.max-request-size=50MB
