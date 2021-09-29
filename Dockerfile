FROM ubuntu:18.04

ENV SBT_VERSION 1.5.1
ENV SCALA_VERSION 2.12.12

ENV TZ=Europe/Warsaw

RUN apt-get update && apt-get upgrade -y
RUN apt install npm -y
RUN apt-get install -y openjdk-8-jdk
RUN apt-get install -y build-essential wget curl zip unzip

RUN useradd -ms /bin/bash jela
RUN adduser jela sudo

USER jela
WORKDIR /home/jela/

RUN curl -s "https://get.sdkman.io" | bash
RUN chmod a+x "/home/jela/.sdkman/bin/sdkman-init.sh"
RUN bash -c "source /home/jela/.sdkman/bin/sdkman-init.sh && sdk install sbt $SBT_VERSION"
RUN bash -c "source /home/jela/.sdkman/bin/sdkman-init.sh && sdk install scala $SCALA_VERSION"

RUN mkdir shop
WORKDIR /home/jela/shop/

EXPOSE 3000
EXPOSE 9000
