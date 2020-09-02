FROM java:8
EXPOSE 8792
VOLUME /tmp
ADD file-storage-server-0.0.1-SNAPSHOT.jar file-storage-server.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/file-storage-server.jar" ]