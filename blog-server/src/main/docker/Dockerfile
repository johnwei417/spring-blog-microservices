FROM java:8
EXPOSE 8762
VOLUME /tmp
ADD blog-server-0.0.1-SNAPSHOT.jar blog-server.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/blog-server.jar" ]