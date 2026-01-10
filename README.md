<div align="center">
  <img src="assets/logo.png" alt="tableSentinel Logo" width="334">
  <p>
    <strong>eBPF/XDP + netfilter 하이브리드 기반의 리눅스 방화벽(Firewall) 프로젝트</strong>
  </p>
  <img src="https://img.shields.io/badge/Python-3776AB?style=flat-square&logo=python&logoColor=white">
  <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white">
  <img src="https://img.shields.io/badge/Vue.js-4FC08D?style=flat-square&logo=vuedotjs&logoColor=white">
  <img src="https://img.shields.io/badge/eBPF_XDP-FFA500?style=flat-square">
  <br>
</div>


# tableSentinel Project
tableSentinel은 패킷의 L2/L3 영역은 xdp-filter를 통해 오프로딩하여 처리하며, L4 영역은 Netfilter로 유연한 처리 파이프라인을 구성하여 엣지(Edge) 서버나 Standalone 서버의 DDoS등의 외부 공격을 감지하고 방어하기 위한 프로젝트입니다.

- **⚠️ Disclaimer (PoC 단계 안내)** 본 프로젝트는 eBPF/XDP와 netfilter 통합 제어 파이프라인을 검증하기 위한 PoC(Proof of Concept) 단계입니다. 현재 통신 구간인 에이전트 ⇋ 백엔드의 gRPC와 백엔드 ⇋ 프론트엔드의 REST API 통신 파이프라인을 암호화하지 않았으므로, **프로덕션 환경에서의 사용은 권장하지 않습니다.**

## Core Architecture
- **L2/L3 영역 (eBPF/XDP)**: xdp-filter를 활용하여 패킷의 커널 진입 전(sk_buff 생성전) 단계에서 고속차단(Offloading) 수행
- **L4 영역 (netfilter)**: 패킷의 sk_buff 전달 이후 netfilter의 기능을 활용한 유연한 처리 수행

## 프로젝트 구성
본 프로젝트는 다음과 같은 구성으로 되어있습니다.
### 주요 기능
- **XDP Native Mode**를 활용한 초고속 패킷 필터링
- **netfilter** 기반 방화벽과 연계로 지능적 패킷 필터링 구현
- **Wrapping** 구조로 추후 에이전트의 애플리케이션 변경점 영향도 낮음
- **gRPC 구성**으로 기존 Polling 방식의 응답속도 개선
- **3-Tier Layered Design**으로 확장성 및 유지보수성 확보

### Tech Stack
- **Kernel(Core):** eBPF/XDP, netfilter
- **Agent:** Python
- **Data Pipeline:** REST API, gRPC
- **Backend:** Spring Boot (REST API, gRPC)
- **Frontend:** Vue.js + TailAdmin (Template)
- **CI/CD:** terraform, cloud-init, ansible, Docker Compose, awscli

## Installation
### 요구사항 (준비중)
 - XDP Native 또는 Offload 기능 사용시 호환되는 NIC 필요
 - 에이전트 실행을 위한 Docker가 설치된 호스트(또는 Dockerfile에 기재된 Python 구성 필요 - 추후 통합 예정)
### 설치방법 (준비중)
 - 현재 단계에서는 /docker/agent.Dockerfile 빌드 후 사용

## 계획
### v0.2.0(Current) - 2026/01/04
 - ☑︎ Core: XDP 및 nftables 기반 차단 로직 구현 (완료)
 - ☑︎ Network: 백엔드-에이전트 간 gRPC 스트리밍 통신 구현 (완료)
 - ☑︎ UI: Vue.js 기반 대시보드 및 실시간 제어 연동 (완료)

### Future Plans
 - ☐ Intelligence: 패킷 패턴 분석을 통한 자동 차단 (DDoS 감지)
 - ☐ Security: 통신 구간(mTLS) 암호화 및 API 인증 강화
 - ☐ Performance: 에이전트 코어 로직 Rust로 포팅 (메모리 안전성 확보)
 - ☐ Logging & Audit : DB연동을 통한 대시보드 접근 제어 및 로그/감사 처리

## License & Credits
The tableSentinel is released under the MIT License.
See [LICENSE](LICENSE) file for details.

### Third Party Notices
This project uses the following open source software:

#### Frontend (Vue.js)
* **Vue.js** (MIT License) - Copyright (c) 2015-present Evan You
* **TailAdmin** (MIT License / Free) - Copyright (c) 2023 TailAdmin
* **Axios** (MIT License)

#### Backend (Spring Boot)
* **Spring Boot** (Apache License 2.0)
* **Netty** (Apache License 2.0)
* **gRPC** (Apache License 2.0)

#### Agent (Python & Kernel)
* **xdp-tools** (GPL-2.0, LGPL-2.1 and BSD-2-Clause) - *Used via CLI interactions*
* **bpftool** (GPL-2.0, LGPL-2.1 and BSD-2-Clause) - *Used via CLI interactions*
* **nftables** (GPL v2) - *Used via CLI interactions*
