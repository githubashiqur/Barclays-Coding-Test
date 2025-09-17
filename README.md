# Eagle Bank — Tech Task

This repository contains a Spring Boot REST API for a simple banking service (accounts, transactions, users, auth). The OpenAPI contract is in `openapi.yaml` and the application uses SpringDoc (OpenAPI 3) annotations to generate the runtime OpenAPI document.

This README documents how to build, run, test the application.

## Table of contents
- Prerequisites
- Build & run
- Run from IDE
- Testing
- API documentation (OpenAPI / Swagger)
- Project layout

## Prerequisites
- Java 17+ (the project is compatible with Java 17+; check `pom.xml` for exact Java target)
- Maven 3.6+
- Git (optional)

On Windows PowerShell, verify:
```powershell
java -version
mvn -v
```

## Build
From the repository root:
```powershell
mvn clean install
```

This produces `target/bank-0.0.1-SNAPSHOT.jar`.

## Run
Run the packaged jar:
```powershell
java -jar target\bank-0.0.1-SNAPSHOT.jar
```

Alternatively, the application can be run using 
```powershell
mvn spring-boot:run
```

Application default port: `8080` (override via `application.properties` or environment variables if needed).

## Run in IDE
Import the Maven project into IntelliJ IDEA or VS Code Java extension and run `com.example.bank.BankApplication`.

## Tests
Run the test suite:
```powershell
mvn test
```

See test reports under `target/surefire-reports` when tests run.

## API documentation (OpenAPI / Swagger)
- The repository includes a declarative `openapi.yaml` describing the expected API contract.
- The running application also exposes a generated OpenAPI document (using SpringDoc). Common endpoints:
	- JSON OpenAPI: `http://localhost:8080/v3/api-docs`
	- Swagger UI: `http://localhost:8080/swagger-ui/index.html` or `http://localhost:8080/swagger-ui.html` (depending on config)

To verify the `POST /v1/users` operation documentation:
1. Start the app
2. Open `http://localhost:8080/swagger-ui/index.html` 
3. Modify the username, password and email to your preference and click on execute
4. A correct request should return a 201 response

Similarly you can try out the other endpoints, as per the instruction sheet.


## Project layout
- `openapi.yaml` — the OpenAPI 3 spec in the repo root
- `pom.xml`, `mvnw` — Maven build
- `src/main/java/com/example/bank` — application code
	- `controller/` — REST controllers
	- `dto/` — request/response DTOs
	- `service/` — business logic
	- `repository/` — data storage interfaces
	- `security/` — JWT/security utilities
	- `exception/` — custom exceptions & global handler
- `src/main/resources/application.properties` — app config
