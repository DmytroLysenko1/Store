# Store Management Application

## Overview

The Store Management Application is a comprehensive solution designed to manage product catalogs, including CRUD operations for products, user authentication, and authorization. This application is built using modern Java technologies and follows best practices for software development, making it suitable for deployment in various retail and e-commerce environments.

## Features

- **Product Management**: Create, read, update, and delete products in the catalog.
- **User Authentication and Authorization**: Secure access to different parts of the application based on user roles.
- **Integration with External Services**: Uses WireMock for mocking external HTTP services during testing.
- **Database Integration**: Connects to a PostgreSQL database for persistent storage.
- **RESTful API**: Exposes RESTful endpoints for managing products.
- **Testing**: Comprehensive unit and integration tests using JUnit and Spring Boot Test.

## Technologies Used

- **Java**: The primary programming language used for the application.
- **Spring Boot**: Framework for building the application, providing dependency injection, web framework, and more.
- **Spring Security**: For securing the application with authentication and authorization.
- **Spring Data JPA**: For database interactions using JPA.
- **Spring WebFlux**: For building reactive web applications.
- **PostgreSQL**: The relational database used for persistent storage.
- **Flyway**: For database migrations.
- **Maven**: Build automation tool used for managing dependencies and building the project.
- **JUnit**: Testing framework for unit and integration tests.
- **WireMock**: For mocking HTTP services in tests.
- **MockMvc**: For testing Spring MVC controllers.
- **OAuth2**: For handling OAuth2 authentication.
- **JWT**: For handling JSON Web Tokens.
- **Mockito**: For mocking dependencies in tests.
- **Thymeleaf**: Template engine for rendering views.
- **Lombok**: For reducing boilerplate code.
- **H2 Database**: In-memory database used for testing.
- **SLF4J**: Simple Logging Facade for Java.
- **Logback**: Logging framework.
- **Docker**: For containerizing the application.
- **Git**: Version control system.
- **IntelliJ IDEA**: Integrated Development Environment (IDE) used for development.

## Project Structure

- **`src/main/java`**: Contains the main application code.
- **`src/test/java`**: Contains the test code.
- **`src/main/resources`**: Contains configuration files and static resources.
- **`src/test/resources`**: Contains test configuration files and resources.

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL 13 or higher

### Installation

1. **Clone the repository**:
    ```sh
    git clone https://github.com/DmytroLysenko1/Store.git
    cd Store
    ```

2. **Configure the database**:
   Update the database configuration in `application.yaml`:
    ```yaml
    spring:
      datasource:
        url: jdbc:postgresql://localhost:5433/manager
        username: manager
        password: manager
      jpa:
        hibernate:
          ddl-auto: update
    ```

3. **Build the project**:
    ```sh
    mvn clean install
    ```

4. **Run the application**:
    ```sh
    mvn spring-boot:run
    ```

### Running Tests

To run the tests, use the following command:
```sh
mvn test