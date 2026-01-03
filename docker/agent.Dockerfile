FROM ubuntu:24.04
# AWS에서 Image 불러올 경우
# FROM public.ecr.aws/ubuntu/ubuntu:24.04
# 
#
# Build 명령어
# sudo docker build -t tbsen-agent:dev -f agent.Dockerfile .
#
# Test 명령어
# sudo docker run --rm -it --network=host --privileged -v /sys/fs/bpf:/sys/fs/bpf -v $(pwd):/app tbsen-agent:dev
#

# 환경 설정
ENV DEBIAN_FRONTEND=noninteractive

# 필수 패키지 설치
RUN apt update && apt install -y \
    gnupg \
    git \
    curl \
    vim \
    net-tools \
    wget \
    sudo \
    nftables \
    iproute2 \
    iputils-ping \
    build-essential \
    python3 \
    python3-pip \
    libbpf-dev \
    xdp-tools \
    && apt clean \
    && rm -rf /var/lib/apt/lists/*

# 작업 공간 설정
WORKDIR /app

# 명령어 추가
RUN pip3 install grpcio grpcio-tools protobuf --break-system-packages

# UUID DIR 설정
RUN mkdir -p /etc/tbsen-agent

# 소스코드 복사
COPY filter_pb2.py .
COPY filter_pb2_grpc.py .
COPY tbsen_executor.py .
COPY tbsen_parser.py .
COPY tbsen_agent.py .

# 컨테이너 실행 시 bash 셸 시작
CMD ["python3", "tbsen_agent.py"]