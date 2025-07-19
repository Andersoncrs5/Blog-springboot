# RESTful Blog Application with Spring Boot and Java

**Status:** on going

## Overview

This project is a RESTful API developed using **Spring Boot 3.4.7**, designed for managing blog posts, 
users, categories, and comments. It includes features such as:

- JWT-based authentication and role-based access control
- Password recovery via email
- System for favorite posts/comments
- User configuration and category preferences
- Robust test coverage (unit, integration, partial E2E)

The application follows clean architecture and standard CRUD principles, using **Spring Data JPA** for 
database operations and connecting to a **PostgreSQL 17.4** database. **Spring Security + JWT** ensure 
robust authentication and authorization.

---

## Technologies Used

- **Java JDK 23** – Programming language
- **Spring Boot 3.4.7** – Core framework
- **Spring Data JPA** – ORM for database
- **PostgreSQL 17.4** – Relational database
- **Lombok** – Reduces boilerplate
- **Spring Security** – Authentication & authorization
- **JWT (JSON Web Token)** – Stateless auth system
- **Swagger 2.8.9** – API documentation (Springdoc OpenAPI)
- **Redis** – In-memory cache for sessions or tokens

---

## Features

- **JWT-based authentication** (login/registration)
- **Role-based access control** (admin, user, etc.)
- **CRUD for:**
    - Users
    - Blog Posts
    - Categories
    - Comments
- ️ **Favorites system** (posts & comments)
-  **Password recovery via email**
- ️ **User Configuration (`UserConfig`)** – frontend preferences, theme, etc.
-  **User Preferences (`UserPreference`)** – preferred categories for personalized feed
-  **Global Exception Handling**
-  **Pagination & filtering**
-  **Password encryption (BCrypt)**
-  **Testing coverage:**
    - 100% **unit tests**
    - Full **integration tests**
    - Partial **E2E tests**
-  **aws s3** - to file and image upload 

---

##  Possible Future Improvements

- ️ **Media system** for posts (images, videos)
-  CORS config for frontend/backend separation
-  **Analytics & metrics system** (e.g., most liked posts, user activity)
-  Full E2E test coverage
-  Spring actuator

---

##  Structure Overview (Simplified)