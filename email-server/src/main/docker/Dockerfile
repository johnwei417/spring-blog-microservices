FROM java:8
EXPOSE 9394
VOLUME /tmp
ADD email-server-0.0.1-SNAPSHOT.jar email-server.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/email-server.jar" ]