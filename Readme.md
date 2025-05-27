# SuperDuperMart 🛒

A scalable e-commerce platform built with a **microservices architecture**, featuring robust JWT authentication, Redis caching for blazing-fast performance, seamless Stripe payments, and an interactive chatbot for customer support.

---

## 🌟 Overview

SuperDuperMart is designed for high performance and scalability, leveraging a distributed system to handle various e-commerce functionalities. Our architecture ensures that each service is independent, making the platform resilient and easy to maintain.

![Architecture Diagram](System_Structure_Diagram.pdf)  
*A high-level overview of the SuperDuperMart microservices architecture.*
---

## 🛠️ Key Technologies

| Component           | Technology                          | Description                                                                  |
|---------------------|-------------------------------------|------------------------------------------------------------------------------|
| **API Gateway** | Spring WebFlux                      | Handles routing, JWT validation, and injects user context into requests.       |
| **User Service** | Spring Boot                         | Manages user accounts and handles JWT-based authentication (stored in HttpOnly cookies). |
| **Catalog Service** | Spring Boot + Redis                 | Provides product CRUD operations with Spring Validation and Redis caching for fast data retrieval. |
| **Core Service** | Spring Boot + Hibernate             | Manages shopping carts, orders, watchlists, and integrates with Stripe for payments. |
| **Assistant Service**| Node.js                             | A simple chatbot service for basic customer interactions (no external AI APIs). |
| **Database** | MySQL + Hibernate                   | Persistent data storage using MySQL with Hibernate for ORM.                  |

---

## ✨ Core Features

### 🔐 Secure Authentication Flow
* **JWT Generation:** User login via the `user-service` generates a **JSON Web Token (JWT)**, securely stored in an **HttpOnly cookie**.
* **API Gateway Validation:** The `api-gateway` intercepts requests, validates the JWT, and injects the user's information into an `X-User-Id` header for downstream services.
* **Role-Based Access:** Future enhancements will include role-based access control to define user permissions.

### ⚡ Performance Optimizations
* **Redis Caching:** Product data within the `catalog-service` is extensively **cached using Redis** to significantly reduce database load and improve response times.
* **Reactive Routing:** The `api-gateway` utilizes **Spring WebFlux** for non-blocking, reactive routing, enhancing overall system throughput and responsiveness.

### 💳 Streamlined Payment Processing
* **Stripe Integration:** The `core-service` seamlessly integrates with the **Stripe API** for secure and efficient payment processing during checkout.
* **Webhook Handling:** Robust webhook handling is implemented to process payment events and update order statuses in real-time.

### 🤖 Interactive Assistant Service
* **Node.js Chatbot:** A dedicated `assistant-service` built with **Node.js** provides basic chat interactions.
* **Mock Responses:** Currently, the chatbot provides mock responses for frequently asked questions, offering a starting point for future AI integration.

---

## 🚀 Getting Started

Follow these steps to get SuperDuperMart up and running on your local machine.

### Prerequisites

Ensure you have the following software installed:

* **Java 17+**
* **MySQL 8.0**
* **Redis 7.x**
* **Node.js 18+** (for the `assistant-service`)

### Installation

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/your-username/SuperDuperMart.git](https://github.com/your-username/SuperDuperMart.git)
    cd SuperDuperMart
    ```

2.  **Configure your environment:**
    * Set up your MySQL database and update the database connection properties in each service's `application.properties` or `application.yml` file.
    * Ensure your Redis instance is running and accessible.

3.  **Build and run individual services:**
    Navigate into each service directory and run them. For example, to start the `catalog-service`:

    ```bash
    cd catalog-service
    mvn spring-boot:run
    ```
    Repeat this for `user-service`, `core-service`, and `api-gateway`.

4.  **Run the Assistant Service:**
    ```bash
    cd assistant-service
    npm install
    node index.js
    ```

Once all services are running, you can access the SuperDuperMart platform through the `api-gateway`.

---