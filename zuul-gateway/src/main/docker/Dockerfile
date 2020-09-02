FROM java:8
EXPOSE 10000
VOLUME /tmp
ADD zuul-gateway-0.0.1-SNAPSHOT.jar zuul-gateway.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/zuul-gateway.jar" ]