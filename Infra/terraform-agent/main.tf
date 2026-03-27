terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

data "http" "myip" {
  url = "https://ipv4.icanhazip.com"
}

provider "aws" {
  region = var.aws_region
}

data "aws_vpc" "default" {
  default = true
}

resource "aws_key_pair" "deployer" {
  key_name   = var.key_name
  public_key = var.ansible_public_key
}

resource "aws_instance" "agent_node" {
  ami           = "ami-0a71e3eb8b23101ed"
  instance_type = "t3.small"
  key_name      = aws_key_pair.deployer.key_name
  vpc_security_group_ids = [aws_security_group.fresh_sg.id]

  user_data = templatefile("cloud-init.yaml.tpl", {
    ansible_pub_key = var.ansible_public_key
  })

  tags = {
    Name = "tableSentinel-Agent"
  }
}

resource "aws_security_group" "fresh_sg" {
  name        = "tablesentinel_no_vpn_sg"
  description = "Allow SSH Direct Access"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["${chomp(data.http.myip.response_body)}/32"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "local_file" "ansible_inventory" {
  content = templatefile("inventory.tpl", {
    ip_addr  = aws_instance.agent_node.public_ip 
    key_path = var.ansible_private_key_path
    server_addr = var.server_addr
  })
  filename = "./inventory.ini"
}

resource "null_resource" "run_ansible" {
  triggers = {
    instance_id = aws_instance.agent_node.id
  }

  provisioner "local-exec" {
    command = <<EOT
      sleep 90 && \
      ansible-playbook -i inventory.ini deploy_agent.yml
    EOT
  }
  depends_on = [local_file.ansible_inventory]
}