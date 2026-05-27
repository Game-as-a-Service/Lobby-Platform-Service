# Lobby-Platform-Service

Lobby-Platform-Service is the backend service of GaaS, providing a gaming portal and a social community platform. This repository demonstrates advanced software engineering practices and how we leverage modern technologies to build scalable systems. Feel free to explore the code.

## Tech Stack

- Kotlin
- Spring Boot
- MongoDB
- Springdoc-openapi

## Development

### Getting Start

#### Prerequisites

 - JDK 17
 - MongoDB 4.0.2+
 - Optional: Maven, if not using `./mvnw`

#### Building

```bash
# Clone the repository
git clone https://github.com/Game-as-a-Service/Lobby-Platform-Service.git

# Enter the project directory
cd Lobby-Platform-Service

# Build all modules
./mvnw clean install

# Run e2e / integration tests
./mvnw -pl spring -am test
```

#### Running

```bash
# Start the backend service
CLIENT_ID=<your-auth0-client-id> \
CLIENT_SECRET=<your-auth0-client-secret> \
./mvnw -pl spring -am spring-boot:run

# Once running, you can access the Swagger UI at:
http://localhost:8087/swagger-ui/index.html
```

### Project Structure

This project follows DDD tactical design principles with a rich domain model and clear separation of responsibilities. The architecture is structured into three packages: `domain`, `application`, and `spring`, which correspond to the DDD domain, application, and infrastructure layers.

```
Root-
    ├── domain      # Contains domain models, entities, and business logic
    ├── application # Contains use cases and orchestration logic
    └── spring      # Spring-specific configurations and external implementations
```

## Contributors

<a href="https://github.com/Game-as-a-Service/Lobby-Platform/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=Game-as-a-Service/Lobby-Platform"  alt="Contributor"/>
</a>
