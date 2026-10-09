FROM jenkins/jenkins:lts-jdk21
USER root
RUN apt-get update && apt-get install -y --no-install-recommends maven docker-cli curl ca-certificates \
    && rm -rf /var/lib/apt/lists/* \
    && mkdir -p /usr/local/lib/docker/cli-plugins \
    && curl -fsSL https://github.com/docker/compose/releases/download/v5.6.0/docker-compose-linux-x86_64 -o /tmp/docker-compose \
    && curl -fsSL https://github.com/docker/compose/releases/download/v5.6.0/docker-compose-linux-x86_64.sha256 -o /tmp/docker-compose.sha256 \
    && expected="$(cut -d ' ' -f1 /tmp/docker-compose.sha256)" \
    && actual="$(sha256sum /tmp/docker-compose | cut -d ' ' -f1)" \
    && test "$expected" = "$actual" \
    && install -m 0755 /tmp/docker-compose /usr/local/lib/docker/cli-plugins/docker-compose \
    && rm -f /tmp/docker-compose /tmp/docker-compose.sha256
USER jenkins
