FROM java:8
EXPOSE 9393
VOLUME /tmp
ADD user-server-0.0.1-SNAPSHOT.jar user-server.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/user-server.jar" ]