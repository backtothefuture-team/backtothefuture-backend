FROM openjdk:17
RUN mkdir -p /opt/bagtothefuture/config
COPY build/libs/event.jar /opt/bagtothefuture/event.jar
ENTRYPOINT ["java","-jar","/opt/bagtothefuture/event.jar"]