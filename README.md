# MealMate

**MealMate** is a sustainable food waste reduction platform connecting restaurants and stores with the local community. Aligned with **SDG 2 (Zero Hunger)** and **SDG 12 (Responsible Consumption)**.

## Project Overview

- **Goal**: Allow food providers to list surplus food for pickup by the community.
- **Tech Stack**:
    - **Backend**: Java 17, Spring Boot 3, Spring Data JPA, Hibernate, MySQL Database.
    - **Frontend**: Thymeleaf, Bootstrap 5, FontAwesome.

## Features

- **Public Marketplace**: View available food listings.
- **Role-Based Access**:
    - **Providers**: Manage listings (Add, Edit, Delete), View claims.
    - **Consumers**: Browse marketplace, Claim items (Partial or Full quantity).
- **SDG Aligned**: Geared towards reducing waste and feeding the hungry.
- **Persistent Data**: Uses MySQL to store users and listings.

## Prerequisites

- Java 17+
- Maven

## Setup & Running

1.  **Run the Application**:
    ```bash
    .\apache-maven-3.9.6\bin\mvn.cmd spring-boot:run
    ```

2.  **Access the App**:
    - Open browser: [http://localhost:8081](http://localhost:8081)
    - *Note: Database is H2 (In-Memory), so data resets on restart.*

## Sample Credentials

Standard users are pre-loaded (Password: `password`):

- **Consumer**: `consumer@example.com` (Role: Consume Food)
- **Restaurant**: `restaurant@example.com` (Role: Provide Food)

*You can also register a new account on the `/register` page.*

## License

This project is created for educational and social impact purposes.
