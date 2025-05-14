# Number Verification Microservice

This microservice implements the Number Verification CAMARA API, providing telephone number verification functionality for enhanced security and user experience.

## Features

- Phone number verification against user's device
- Device phone number retrieval
- Resilient integration with telecom providers
- Comprehensive logging and metrics
- Security with JWT authentication and rate limiting
- Caching for improved performance

## API Endpoints

- **POST /verify** - Verify if provided phone number matches the user's device
- **GET /device-phone-number** - Retrieve the phone number from the user's device

## Architecture

The service follows a layered architecture:

- **API Layer** - REST controllers and request/response models
- **Service Layer** - Business logic and workflow coordination
- **Integration Layer** - Communication with external telecom providers
- **Persistence Layer** - Data storage for verification logs
- **Security Layer** - Authentication, authorization, and rate limiting
- **Cross-Cutting Concerns** - Logging, metrics, and utilities

## Technology Stack

- **Java 17** - Core programming language
- **Spring Boot 3.2** - Application framework
- **MongoDB 6.0** - Verification log storage
- **Redis 7.0** - Caching and rate limiting
- **Spring Security** - Authentication and authorization
- **Resilience4j** - Circuit breaker and retry for external calls
- **Micrometer** - Metrics collection
- **Prometheus & Grafana** - Monitoring and visualization
- **Docker** - Containerization
- **JUnit 5 & Mockito** - Testing

## Running Locally

1. **Prerequisites**
   - Java 17
   - Docker and Docker Compose

2. **Start the application**
   ```
   docker-compose up -d
   ```

3. **Access the API**
   - Swagger UI: http://localhost:8080/api/v1/swagger-ui.html
   - API Documentation: http://localhost:8080/api/v1/api-docs

4. **Access monitoring**
   - Prometheus: http://localhost:9090
   - Grafana: http://localhost:3000 (admin/admin)

## Testing

Run the tests using:

```
./gradlew test
```

## Configuration

The application can be configured using environment variables:

- `PORT` - Server port (default: 8080)
- `MONGODB_HOST` - MongoDB host (default: localhost)
- `MONGODB_PORT` - MongoDB port (default: 27017)
- `MONGODB_DATABASE` - MongoDB database name (default: verification)
- `REDIS_HOST` - Redis host (default: localhost)
- `REDIS_PORT` - Redis port (default: 6379)
- `JWT_SECRET` - Secret for JWT token validation
- `PRIMARY_PROVIDER_URL` - URL of the primary telecom provider
- `PRIMARY_PROVIDER_KEY` - API key for the primary telecom provider
- `FALLBACK_PROVIDER_URL` - URL of the fallback telecom provider
- `FALLBACK_PROVIDER_KEY` - API key for the fallback telecom provider
- `RATE_LIMIT` - Number of requests allowed per minute (default: 60)
- `LOG_LEVEL` - Logging level (default: INFO)

## Deployment

This service is ready for deployment to Railway cloud. Connect your repository to Railway and it will automatically build and deploy the application.
