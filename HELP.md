# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.3.3.RELEASE/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.3.3.RELEASE/maven-plugin/reference/html/#build-image)
* [Java Mail Sender](https://docs.spring.io/spring-boot/docs/2.3.3.RELEASE/reference/htmlsingle/#boot-features-email)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/2.3.3.RELEASE/reference/htmlsingle/#boot-features-jpa-and-spring-data)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/2.3.3.RELEASE/reference/htmlsingle/#production-ready)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.3.3.RELEASE/reference/htmlsingle/#boot-features-developing-web-applications)
* [Spring for Apache Kafka](https://docs.spring.io/spring-boot/docs/2.3.3.RELEASE/reference/htmlsingle/#boot-features-kafka)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/2.3.3.RELEASE/reference/htmlsingle/#using-boot-devtools)
* [Validation](https://docs.spring.io/spring-boot/docs/2.3.3.RELEASE/reference/htmlsingle/#boot-features-validation)

### Guides
The following guides illustrate how to use some features concretely:

* [Circuit Breaker](https://spring.io/guides/gs/circuit-breaker/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)
* [Service Registration and Discovery](https://spring.io/guides/gs/service-registration-and-discovery/)
* [Routing and Filtering](https://spring.io/guides/gs/routing-and-filtering/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)
* [Client-side load-balancing with Ribbon and Spring Cloud](https://spring.io/guides/gs/client-side-load-balancing/)
* [Accessing data with MySQL](https://spring.io/guides/gs/accessing-data-mysql/)

# Spring Cloud Netflix Maintenance Mode

The dependencies listed below are in maintenance mode. We do not recommend adding them to
new projects:

*  Ribbon
*  Zuul
*  Hystrix
*  Hystrix Dashboard

The decision to move most of the Spring Cloud Netflix projects to maintenance mode was
a response to Netflix not continuing maintenance of many of the libraries that we provided
support for.

Please see [this blog entry](https://spring.io/blog/2018/12/12/spring-cloud-greenwich-rc1-available-now#spring-cloud-netflix-projects-entering-maintenance-mode)
for more information on maintenance mode and a list of suggested replacements for those
libraries.
