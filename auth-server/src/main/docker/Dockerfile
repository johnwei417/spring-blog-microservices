FROM java:8
EXPOSE 9890
VOLUME /tmp
ADD auth-server-0.0.1-SNAPSHOT.jar auth-server.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/auth-server.jar" ]