FROM ubuntu:14.10
MAINTAINER tom@tom-fitzhenry.me.uk

RUN echo "deb http://archive.ubuntu.com/ubuntu utopic main universe" >> /etc/apt/sources.list
RUN apt-get update
RUN apt-get install -y curl openjdk-7-jre openjdk-7-jdk wget

ADD target/scala-2.10/ct-watch-http-scala-assembly-0.13.jar .

CMD java -server -Xms300M -Xmx750M -jar ct-watch-http-scala-assembly-0.13.jar
