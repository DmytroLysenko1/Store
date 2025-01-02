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

- **Java**: The primary programming language used for the application, chosen for its robustness, portability, and extensive ecosystem.
- **Spring Boot**: Used to build the application, providing dependency injection, a web framework, and various utilities to simplify development.
- **Spring Security**: Secures the application with authentication and authorization, ensuring that only authorized users can access certain parts of the application.
- **Spring Data JPA**: Facilitates database interactions using JPA, allowing for easy and efficient data access and manipulation.
- **Spring WebFlux**: Used for building reactive web applications, enabling non-blocking, asynchronous processing for better performance under load.
- **PostgreSQL**: The relational database used for persistent storage, chosen for its reliability, scalability, and support for advanced SQL features.
- **MongoDB**: NoSQL database used for flexible and scalable data storage, suitable for handling large volumes of unstructured data.
- **Flyway**: Manages database migrations, ensuring that the database schema is versioned and consistent across different environments.
- **Maven**: Build automation tool used for managing dependencies, building the project, and running tests, providing a standardized build process.
- **JUnit**: Testing framework for unit and integration tests, ensuring that the application is thoroughly tested and reliable.
- **WireMock**: Mocks HTTP services in tests, allowing for isolated and repeatable testing of components that interact with external services.
- **MockMvc**: Tests Spring MVC controllers, providing a way to test web layer components without starting a full web server.
- **OAuth2**: Handles OAuth2 authentication, enabling secure and standardized authentication mechanisms.
- **JWT**: Manages JSON Web Tokens, providing a compact and secure way to transmit information between parties.
- **Mockito**: Mocks dependencies in tests, allowing for isolated unit testing by simulating the behavior of complex dependencies.
- **Thymeleaf**: Template engine for rendering views, enabling dynamic generation of HTML content based on server-side data.
- **Lombok**: Reduces boilerplate code, simplifying the development process by automatically generating common methods like getters and setters.
- **H2 Database**: In-memory database used for testing, providing a lightweight and fast database solution for running tests.
- **SLF4J**: Simple Logging Facade for Java, providing a standardized logging API that can work with various logging frameworks.
- **Logback**: Logging framework used for logging application events, chosen for its performance and flexibility.
- **Docker**: Containerizes the application, ensuring consistent environments across development, testing, and production.
- **Git**: Version control system used for tracking changes in the source code, facilitating collaboration and version management.
- **ModelMapper**: Object mapping library used to simplify the conversion of objects from one type to another, reducing boilerplate code and improving maintainability.
- **Testcontainers**: Provides lightweight, disposable instances of common databases, Selenium web browsers, or anything else that can run in a Docker container, used for integration testing.
- **Spring REST Docs**: Documents RESTful services by combining hand-written documentation with auto-generated snippets produced with Spring MVC Test or WebTestClient.
- **Reactor Test**: Provides utilities for testing reactive applications built with Project Reactor, ensuring that reactive streams behave as expected.

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