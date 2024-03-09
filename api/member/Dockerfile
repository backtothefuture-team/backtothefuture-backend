FROM openjdk:17
RUN mkdir -p /opt/bagtothefuture/config
COPY build/libs/member.jar /opt/bagtothefuture/member.jar
ENTRYPOINT ["java","-jar","/opt/bagtothefuture/member.jar"]