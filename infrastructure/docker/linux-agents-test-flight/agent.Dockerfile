FROM ubuntu:24.04

ENV DEBIAN_FRONTEND=noninteractive

# 필수 패키지 설치
RUN apt update && apt-get install -y \
    gnupg \
    git \
    curl \
    vim \
    net-tools \
    wget \
    sudo \
    nftables \
    iproute2 \
    python3 \
    python3-pip \
    libbpf-dev \
    xdp-tools \
    && apt clean \
    && rm -rf /var/lib/apt/lists/*

# 작업 공간 설정
WORKDIR /app

# 명령어 추가
RUN pip3 install requests --break-system-packages

COPY tbsen_executor.py .
COPY tbsen_parser.py .
COPY tbsen_agent.py .

# 컨테이너 실행 시 bash 셸 시작
CMD ["python3", "-u", "tbsen_agent.py"]