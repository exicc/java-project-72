FROM eclipse-temurin:21-jdk

ARG GRADLE_VERSION=8.2

WORKDIR /app

COPY /app .

RUN gradle installDist

CMD ./build/install/app/bin/app
