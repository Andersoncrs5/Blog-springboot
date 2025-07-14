# RESTful Blog Application with Spring Boot and Java

Status: on going

##  Overview

    This project is a RESTful API developed using Spring Boot 3.4.7, designed for managing blog posts, users,
    categories, and comments. It includes features such as a favorite posts system, JWT-based authentication, 
    password recovery via email, and robust testing coverage (unit, integration, and partial E2E).
    
    The application follows standard CRUD principles and uses Spring Data JPA for database operations, connecting 
    to a PostgreSQL 17.4 relational database. Security is enforced using Spring Security with JWT for authorization.

## Technologies Used

    Java JDK 23 – Programming language.

    Spring Boot 3.4.7 – Core framework for building RESTful APIs.

    Spring Data JPA – ORM for database interaction.

    PostgreSQL 17.4 – Relational database.

    Lombok – Reduces boilerplate code via annotations.

    Spring Security – Security and authentication framework.

    JWT (JSON Web Token) – Token-based user authentication.

    Swagger 2.8.8 – API documentation (Springdoc OpenAPI).

    Redis 

## Features

    JWT-based user authentication (login and registration).

    Full CRUD operations for Users, Posts, Categories, and Comments.

    Favorite posts system (users can mark posts as favorites).

    Password recovery via email.

    Global exception handling.

    Pagination and filtering system.

    Data encryption (e.g., password hashing).

    RESTful API architecture with standardized endpoints.

    Comprehensive testing:

        Unit tests – 100% coverage.

        Integration tests – fully implemented.

        End-to-end (E2E) tests – partial implementation.


# Possible Future Improvements

    Integration with Redis (caching).

    More complete E2E test coverage.

    CORS configuration for frontend/backend separation.

    Analytical data modeling and metrics system.