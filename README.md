<div align="center">
  <img src="assets/logo.png" alt="tableSentinel Logo" width="1080">
  <p>
    <strong>eBPF/XDP + netfilter 기반의 리눅스 방화벽 및 통합 관제 시스템</strong>
  </p>
  <img src="https://img.shields.io/badge/Python-3776AB?style=flat-square&logo=python&logoColor=white">
  <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white">
  <img src="https://img.shields.io/badge/Vue.js-4FC08D?style=flat-square&logo=vuedotjs&logoColor=white">
  <img src="https://img.shields.io/badge/eBPF_XDP-FFA500?style=flat-square">
  <br>
</div>


# tableSentinel Project
tableSentinel 프로젝트는 XDP + netfilter을 웹 GUI로 수 많은 Agent를 제어할 수 있는 통합 관제 시스템입니다.

## 목표
- nftables, xdptool 등 방화벽 유기적 연결
- DB를 통한 유저/로그/감사 관리
- 커널 소스를 통한 호스트 방화벽 실시간 모니터링

# 프로젝트 구성
본 프로젝트는 다음과 같은 구성으로 되어있습니다.
## 아키텍쳐
- **Agent:** Python + XDP (eBPF) for Kernel-level Packet Drop + netfilter (iptables, nftables)
- **Backend:** Spring Boot (REST API, Polling Queue)
- **Frontend:** Vue.js (Dashboard) + TailAdmin (UI)
- **Infrastructure:** Docker (Privileged Container)

## 주요 기능
- **XDP Native Mode**를 활용한 초고속 패킷 필터링
- **netfilter** 기반 방화벽과 연계로 지능적 패킷 필터링 구현
- **Wrapping** 구조로 추후 에이전트의 애플리케이션 변경점 영향도 낮음
- **gRPC 구성**으로 기존 Polling 방식의 응답속도 개선
- **3-Tier Layered Design**으로 확장성 및 유지보수성 확보

## 기술 스택(Stack)
- **Language:** Python, Java
- **Design Pattern:** gRPC, REST API
- **Framework:** Spring Boot, Vue.js
- **Core:** eBPF/XDP, netfilter
- **DevOps:** Docker

# 계획(TODO)
## 단기간 계획
 - ☐ 백엔드 및 에이전트 통신 gRPC 리팩터링
 - ☐ 프론트엔드 Test UI → TailAdmin 리팩터링
 - ☑︎ 프로젝트 알파버전 v0.1.0 완성(기능 구현 PoC)

## 장기간 계획
 - ☐ nftables 및 XDP 필터링 지능적 분류
 - ☐ DDoS 의심 패킷 혹은 공격자 패킷 구분 기능 추가
 - ☐ Agent Rust로 전환
 - ☐ 프론트엔드, 백엔드, 에이전트 통신간 암호화 설정
 - ☐ DB 연결을 통한 계정 제어 및 감사

# License & Credits
The tableSentinel is released under the MIT License.
See [LICENSE](LICENSE) file for details.

# Third Party Notices
This project uses the following open source software:

## Frontend (Vue.js)
* **Vue.js** (MIT License) - Copyright (c) 2015-present Evan You
* **TailAdmin** (MIT License / Free) - Copyright (c) 2023 TailAdmin
* **Axios** (MIT License)

## Backend (Spring Boot)
* **Spring Boot** (Apache License 2.0)
* **Netty** (Apache License 2.0)
* **gRPC** (Apache License 2.0)

## Agent (Python & Kernel)
* **xdp-tools** (GPL-2.0, LGPL-2.1 and BSD-2-Clause) - *Used via CLI interactions*
* **nftables** (GPL v2) - *Used via CLI interactions*