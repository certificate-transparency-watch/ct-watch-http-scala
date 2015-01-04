FROM ubuntu:14.10
MAINTAINER tom@tom-fitzhenry.me.uk

RUN echo "deb http://archive.ubuntu.com/ubuntu utopic main universe" >> /etc/apt/sources.list
RUN apt-get update
RUN apt-get install -y curl openjdk-7-jre openjdk-7-jdk wget

RUN wget https://github.com/tomfitzhenry/ct-watch-http-scala/releases/download/0.3/ct-watch-http-scala-assembly-0.3.jar.tar.gz
RUN tar xf ct-watch-http-scala-assembly-0.3.jar.tar.gz

CMD java -jar ct-watch-http-scala-assembly-0.3.jar
