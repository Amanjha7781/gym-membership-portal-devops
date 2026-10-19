# Ansible is not available natively on Windows. This small runner reuses the
# official Docker CLI image so Ansible can manage the existing Docker Desktop
# engine through its mounted socket.
FROM docker:29-cli

COPY ansible.cfg /etc/ansible/ansible.cfg

RUN apk add --no-cache python3 py3-pip \
    && python3 -m venv /opt/ansible \
    && /opt/ansible/bin/pip install --no-cache-dir ansible-core==2.19.4 \
    && /opt/ansible/bin/ansible --version

ENV PATH="/opt/ansible/bin:${PATH}"
ENV ANSIBLE_NOCOLOR=1
ENV ANSIBLE_CONFIG=/etc/ansible/ansible.cfg

WORKDIR /workspace/deployment/ansible
ENTRYPOINT ["ansible-playbook"]
