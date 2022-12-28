# Java Based microservices

This note address simple implementation and deployment detail of the two microservices used in the demonstration.

## Tenant Manager

This is a Java Quarkus app, with Reactive REST API, persistence to RDS Postgresql via JPA and Panache. We recommend to review the following Quarkus guides to get started on this stack.

    * [GENERATING JAX-RS RESOURCES WITH PANACHE](https://quarkus.io/guides/rest-data-panache).
    * [Reactive messaging](https://quarkus.io/guides/kafka-reactive-getting-started).
    * [Kubernetes extension](https://quarkus.io/guides/deploying-to-kubernetes).

