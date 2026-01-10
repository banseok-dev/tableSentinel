#cloud-config
package_update: true
packages:
  - docker.io
  - git
  - xdp-tools

users:
  - default
  - name: ubuntu
    ssh_authorized_keys:
      - ${ansible_pub_key}

runcmd:
  - systemctl disable --now ufw
  - systemctl enable --now docker