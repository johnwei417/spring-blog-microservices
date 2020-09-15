# Spring Blog Microservices
Blog backend application with micro-services architecture &amp; asynchronous communications using Kafka &amp; Oauth2 Security &amp; Hystrix &amp; ELK 

## Instruction
* Install docker in your local machine, go to dir where you store docker-compose file, and run docker-compose up -d to install and run containers<br>
(you need to config docker compose based on your preferences)
* Go to config folder, and add host names to your local machine at /etc/hosts
* Make sure you have jdk8 and tomcat running in your local machine
* Import the project to your Jetbrain Intellij IDEA to run those microservices

## Modules
* eureka-server: the server providing service registry and discovery
* auth-server: Oauth2 Authorization server
* user-server: the server providing login, register, change password features
* blog-server: the server providing blog business service;
* email-server: the server used for sending email to user
* file-storage-server: the server used for uploading and downloading files
* zuul-gateway: api gateway 
