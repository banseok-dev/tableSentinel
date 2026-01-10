# 프론트엔드/백엔드/에이전트 배포 Docker compose

## 구성
 - back-end.Dockerfile: 백엔드 구성 도커 파일
 - front-end.Dockerfile: 프론트엔드 구성 도커 파일
 - agent.Dockerfile: 에이전트 구성 도커 파일(에이전의 경우 docker compose up -d 명령어로 사용 불가능. 별도 빌드 후 사용)

## 실행
 - 소스코드를 받은 후 docker compose up -d 명령어를 진행하여 배포
 - 설정파일의 포트 변경 비권장(필요시 백엔드 프론트엔드 각 포트 설정 파일 수정 선행 필요)

## 접속
 - Docker에서 deploy한 호스트의 IP 주소로 접속(HTTP)