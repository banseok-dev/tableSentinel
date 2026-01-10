# Agent AWS 배포 자동화 코드

## 구성
 - cloud-init: ssh키 주입 및 기초 설정
 - ansible: 
 - terraform: AWS 인스턴스 배포 및 전반적인 환경 구성

## 실행
 - 1. aws configure 실행하여 다음과 같은 IAM 권한(AmazonEC2FullAcces, AmazonVPCFullAcces)이 할당된 계정 설정
 - 2. terraform.tfvars.example에 따른 값을 기입 후 terraform.tfvars 생성
 - 3. terraform apply 명령어를 통한 실행

## 구성
 - 우분투 24.04 x86
 - AWS EC2 인스턴스 타입 t3.small

## 그외
 - MS Azure에 배포할 경우 azurecli를 이용하여 재구성 필요함
 - 로컬 인프라에서 실행할 경우 프로젝트의 docekr/agnet.Dockerfile의 내용을 확인하여 빌드 후 사용 필요