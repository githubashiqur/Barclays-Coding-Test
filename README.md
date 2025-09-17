# Eagle Bank API

This is a Spring Boot REST API for the Eagle Bank coding test.

## Features

- User registration and authentication (JWT-based)
- Account management
- Transaction management
- Secure endpoints with Spring Security
- OpenAPI (Swagger) documentation

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Data JPA
- Spring Security
- JWT (io.jsonwebtoken)
- H2 (or other) database
- OpenAPI/Swagger

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+

### Build and Run

```sh
./mvnw clean install
./mvnw spring-boot:run
```

The API will be available at:  
`http://localhost:8080`

### API Documentation

After starting the application, access the OpenAPI/Swagger UI at:  
`http://localhost:8080/swagger-ui/index.html`