# Project Description: RESTful Blog Application

Overview:

    This project is a RESTful API built with Spring Boot 3.4.2, designed for managing blog posts, 
    users, categories, and comments. It includes a favorite posts system, allowing users to save their 
    favorite blog posts. The application follows CRUD principles and uses Spring Data JPA for database interactions.

Technologies Used:

    JDK 21 – Java Development Kit.
    Spring Boot 3.4.2 – Framework for building RESTful APIs.
    Spring Data JPA – ORM for database interaction.
    MySQL 8.0.34 – Relational database for data storage.
    Lombok – Reduces boilerplate code with annotations.
    Swagger 2.8.4 – API documentation with Springdoc OpenAPI.

Features

    User authentication (JWT-based login & registration).
    CRUD operations for Users, Posts, Categories, and Comments.
    Favorite Posts System (Users can mark posts as favorites).
    RESTful API architecture with standardized endpoints.

Database Structure:
    
    User (users) → Stores user data.
    Post (posts) → Contains blog posts.~~~~
    Category (categories) → Organizes posts into categories.
    Comment (comments) → Stores comments on posts.
    Favorite Post (favorite_posts) → Links users with their favorite posts.


# Updates

Jwt

spring security

Pagination

crypto

Tables : PostLikes and CommentLikes

Otimizations

Exceptions global