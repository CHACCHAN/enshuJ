FROM mcr.microsoft.com/devcontainers/java:21-bullseye

RUN curl -fsSL https://bun.sh/install | bash \
    && mv /root/.bun/bin/bun /usr/local/bin/bun

RUN mkdir -p /home/vscode/.m2 /home/vscode/.gradle \
    && chown -R vscode:vscode /home/vscode/.m2 /home/vscode/.gradle