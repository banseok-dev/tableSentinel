variable "aws_region" {
    default = "ap-northeast-2"
}

variable "key_name" {
  description = "AWS에 등록할 EC2 키페어 이름"
  type        = string
}

variable "ansible_public_key" {
  description = "Ansible 제어 노드의 SSH Public Key"
  type        = string
}

variable "ansible_private_key_path" {
  description = "로컬 SSH 키 파일 경로"
  type        = string
}

variable "server_addr" {
  description = "백엔드 서버 주소"
  type        = string
}