FROM eclipse-temurin:21-jdk-alpine

RUN apk add --no-cache bash curl unzip \
    && mkdir -p /workspace

RUN curl -fsSL https://bun.sh/install | bash \
    && mv /root/.bun/bin/bun /usr/local/bin/bun
