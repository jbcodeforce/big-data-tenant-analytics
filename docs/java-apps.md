# Java Based microservices

This note address simple implementation and deployment detail of the two microservices used in the demonstration.

## Tenant Manager

This is a Java Quarkus app, with Reactive REST API, persistence to RDS Postgresql via JPA and Panache. We recommend to review the following Quarkus guides to get started on this stack.

* [GENERATING JAX-RS RESOURCES WITH PANACHE](https://quarkus.io/guides/rest-data-panache).
* [Reactive messaging](https://quarkus.io/guides/kafka-reactive-getting-started).
* [Kubernetes extension](https://quarkus.io/guides/deploying-to-kubernetes).

The service supports basic CRUD operation on the Tenant entity. Panache is doing the JPA mapping to JDBC. The OpenAPI extension is added to offer a swagger User interface so it easier to test the component. We use the Repository pattern for the Panache.


## Run locally

* Start docker compose for a local postgresql

```sh
docker compose up -d
```

* Start quarkus in dev mode: `quarkus dev` if you have the CLI, or `mvn dev` with maven.


## Build docker image and push to ECR

