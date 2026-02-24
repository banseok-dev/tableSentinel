<div align="center">
  <img src="assets/logo.png" alt="tableSentinel Logo" width="334">
  <p>
    <strong>eBPF/XDP + netfilter 하이브리드 기반의 리눅스 방화벽(Firewall) 프로젝트</strong>
  </p>
</div>

# tableSentinel Project

tableSentinel은 패킷의 L2/L3 영역은 xdp-filter를 통해 오프로딩하여 처리하며, L4 영역은 Netfilter로 유연한 처리 파이프라인을 구성하여 엣지(Edge) 서버나 Standalone 서버의 DDoS등의 외부 공격을 감지하고 방어하기 위한 프로젝트입니다.

- **⚠️ Disclaimer (PoC 단계 안내)** 현재 통신 구간인 에이전트 ⇋ 백엔드의 gRPC와 백엔드 ⇋ 프론트엔드의 REST API 통신 암호 파이프라인 설계 진행중이므로, **프로덕션 환경에서의 사용은 권장하지 않습니다.**

<div align="center">
  <img src="assets/project_diagram.png" alt="tableSentinel diagram" width="1080">
</div>

- **[상세 아키텍처 라이프사이클 확인](https://github.com/banseok-dev/tableSentinel/blob/main/docs/project-lifecycle.md)**
- **[프로젝트 ADR 확인](https://banseok.dev/%ec%8b%9c%ec%8a%a4%ed%85%9c/tablesentinel-adr/)**

## Require & Run

- Agent
  - Rust-Standalone - Linux Kernel >= 5.10 (예정)
  - Python -> Docker

- Backend
  - Case.Container -> Docker
  - Case.Standalone - [back-end.Dockerfile](./docker/back-end.Dockerfile), 코드에 기재된 구성요소 필요

- Frontend
  - Case.Container -> Docker
  - Case.Standalone - [front-end.Dockerfile](./docker/front-end.Dockerfile), 코드에 기재된 구성요소 필요

## Plan

### v0.3.0(Current) - 2026/02/04 ~

- ☐ Agent: Rust 변경 및 구조 개선
- ☐ Security: 통신 구간(mTLS) 암호화 및 API 인증 강화

### v0.2.0(Checkout) - 2026/01/04

- ☑︎ Core: XDP 및 nftables 기반 차단 로직 구현 (완료)
- ☑︎ Network: 백엔드-에이전트 간 gRPC 스트리밍 통신 구현 (완료)
- ☑︎ UI: Vue.js 기반 대시보드 및 실시간 제어 연동 (완료)

### Future Plans

- ☐ Intelligence: 패킷 패턴 분석을 통한 자동 차단
- ☐ Logging & Audit : DB연동을 통한 대시보드 접근 제어 및 로그/감사 처리

## License & Credits

The tableSentinel is released under the AGPL License.
See [LICENSE](LICENSE) file for details.

### Third Party Notices

This project uses the following open source software:

#### Frontend (Vue.js)

- **Vue.js** (MIT License) - Copyright (c) 2015-present Evan You
- **TailAdmin** (MIT License / Free) - Copyright (c) 2023 TailAdmin
- **Axios** (MIT License)

#### Backend (Spring Boot)

- **Spring Boot** (Apache License 2.0)
- **Netty** (Apache License 2.0)
- **gRPC** (Apache License 2.0)

#### Agent (Python & Kernel)

- **xdp-tools** (GPL-2.0, LGPL-2.1 and BSD-2-Clause) - _Used via CLI interactions_
- **bpftool** (GPL-2.0, LGPL-2.1 and BSD-2-Clause) - _Used via CLI interactions_
- **nftables** (GPL v2) - _Used via CLI interactions_
