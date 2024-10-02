# Modsen Library API - Microservices Architecture with Spring Boot

## Overview

The **Modsen Library API** is a microservice-based system designed for managing books and users. Built using **Spring Boot** and a range of modern technologies, the project leverages the power of **Kafka** for messaging, **Keycloak** for secure authentication, and **Grafana Stack** for comprehensive monitoring and tracing. The project can run both locally using Docker Compose and in a Kubernetes environment, making it highly flexible and scalable.

### Key Features:

- **User Authentication**: Handled via **Keycloak**, supporting both `USER` and `ADMIN` roles.
- **Book Management**: The **Book-service** enables book creation, deletion, and modification, and communicates with the **Library-service** via **Kafka** for real-time synchronization of book status.
- **Library-service**: Tracks book availability and handles Kafka messages regarding book updates.
- **Centralized Gateway**: **Gateway-service** provides a unified entry point for all API interactions, routing through to various microservices.
- **Monitoring and Tracing**: Integrated with **Grafana Stack** for complete observability of logs, metrics, and distributed traces.

---

## Technology Stack

The **Modsen Library API** utilizes the following core technologies:

- **Java 21** and **JDK 21**: Powering all microservices.
- **Spring Boot**: Foundation of the microservices.
- **Spring Cloud**: Enables microservice coordination and configuration management.
- **Kafka**: Facilitates asynchronous communication between services.
- **Keycloak**: Provides user authentication and authorization.
- **Swagger**: API documentation and testing.
- **Docker** and **Docker BuildKit**: Containerization of microservices with optimized builds.
- **Kubernetes (Minikube)**: Orchestrating containers for production-like deployment.
- **Helm**: Package manager for Kubernetes.
- **Grafana Stack**:
    - **Grafana**: Dashboard for monitoring metrics and logs.
    - **Prometheus**: Metrics collection.
    - **Loki**: Centralized logging.
    - **Tempo**: Request tracing.
    - **Jaeger** & **Zipkin**: Distributed tracing.
    - **OpenTelemetry Collector**: Aggregating metrics and traces for observability.

---

## Installation & Setup

### Clone the Repository

```bash
git clone https://github.com/MikalaiSamtsevich/modsen-library-api.git
cd modsen-library-api
```

---

### Local Setup

#### Start required tools:

Launch the necessary infrastructure services (e.g., Kafka, Keycloak) using Docker Compose:

```bash
cd docker
docker-compose -f compose-tools.yaml up
```

#### Start Configuration Services:

1. Launch `config-service` first.
2. Start `eureka-service` next to register all microservices.

#### Start the Microservices:

After `config-service` and `eureka-service` are up, start all other services (e.g., `book-service`, `library-service`, `gateway-service`) via **Gradle**:

```bash
./gradlew bootRun
```

#### Access the Swagger Documentation:

You can view the API documentation for all services via the **Gateway-service** at `http://localhost:8091/webjars/swagger-ui/index.html`.  
Each microservice has its own Swagger documentation accessible through individual URLs.

---

### User Authentication

1. Register or log in through **keycloak-auth-service**.
    - New users get assigned the `USER` role by default.
    - For `ADMIN` privileges, you need to update the role directly via the Keycloak interface.

2. **Pre-configured users**:
    - **user/user** (`USER` role).
    - **admin/admin** (`ADMIN` & `USER` roles).

3. **JWT Token**: Upon successful login, you will receive an `access_token`.
    - Use this token for authenticated requests to other services.
    - To test book creation or deletion, first log in with **admin/admin**, then copy the `access_token` and use it in the `Authorization` header for subsequent API requests.

---

### Running with Docker Compose

For running the application using Docker, the services are fully containerized and leverage **Docker BuildKit** for faster builds.

```bash
cd docker
docker-compose -f compose-app.yaml build
docker-compose -f compose-app.yaml up
```

This will launch all microservices, tools (Kafka, Keycloak), and dependencies in Docker containers.

---

### Running in Kubernetes with Minikube

#### Start Minikube:

Ensure Minikube is running with **at least 4 CPUs**, as lower configurations can cause issues during deployment.

```bash
minikube start --cpus=4
```

#### Install Helm:

Install **Helm** to manage Kubernetes deployments. Instructions can be found at [Helm Installation](https://helm.sh/docs/intro/install/).

#### Install Strimzi Kafka Operator:

Strimzi simplifies Kafka deployment and management in Kubernetes.

```bash
helm repo add strimzi https://strimzi.io/charts
helm repo update
helm install strimzi strimzi/strimzi-kafka-operator
```
(Recommended) Wait for strimzi-kafka-operator ready

#### Deploy Microservices:

Go to the project root directory and deploy all services in Kubernetes by applying the manifests:

```bash
kubectl apply -R -f k8s
```

#### Access Minikube Tunnel:

Once all services are up (this may take a few minutes), run **Minikube Tunnel** to access Swagger, Grafana, and Keycloak:

```bash
minikube tunnel
```

---

### Accessing Services through Gateway

All service requests can be routed through the **Gateway-service** using the base URL: `http://localhost:9091/{service-name}/{service-endpoint}`. The gateway acts as a unified entry point, simplifying the communication with individual services. Below are examples of how to access each service through the gateway.

Examples:

- **Keycloak Auth Service:**
  ```
  http://localhost:9091/keycloak-auth-service/auth/login
  ```
- **Book Service:**
  ```
  http://localhost:9091/book-service/books
  ```

- **Library Service:**
  ```
  http://localhost:9091/library-service/books/status
  ```

### Monitoring & Tracing Endpoints

The application is integrated with **Grafana Stack** for complete observability. Here are the key monitoring and tracing endpoints:

- **Grafana Dashboard**: Accessible at `http://localhost:3000` (Login: `admin/admin`).
- **Prometheus**: Collects metrics, available at `http://localhost:9090`.
- **Loki**: Centralized logging, viewable in the Grafana dashboard.
- **Tempo**: Tracks request tracing across services.
- **Jaeger** and **Zipkin**: For distributed tracing.
    - **Jaeger**: `http://localhost:16686`
    - **Zipkin**: `http://localhost:9411`
- **OpenTelemetry Collector**: Aggregates telemetry data from all services and forwards it to the observability tools.

### Keycloak Endpoint

- **Keycloak Administration UI**: Accessible at `http://localhost:9082` (Login: `admin/admin`).

---

## Conclusion

This project follows a modern, microservice-based architecture, using the latest in **Java**, **Spring Boot**, **Kafka**, and **Kubernetes** to provide a scalable, efficient, and observable solution for managing books and users. With real-time messaging, secure user authentication, and comprehensive monitoring, this architecture is both flexible and robust.