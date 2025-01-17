# Leave Management System

---

## Project Description

The Leave Management System is a web-based application developed using Spring Boot, Spring Security, and Thymeleaf. The
system is integrated with AWS services and RDS MySQL for database management.

<br>
It allows employees to apply for leave and submit compensation claims, managers to generate reports, approve or reject leave applications and 
compensation claims, and administrators to manage employees, job designations, 
users and their roles, leave balances, public holidays, and the company hierarchy.

<br>

## Technologies

- **Java Spring Boot**
- **Thymeleaf**
- **MySQL**
- **Docker**
- **Github Actions**
- **AWS Services (EC2, RDS, CloudFront, Route 53)**

## Deployed On AWS

- [https://keiapp.me](https://keiapp.me)

\
![AWS Diagram](/src/main/resources/static/images/aws_system_architecture_diagram.png?raw=true)


## Features

- **Employee**: Apply, update, and cancel leave,
- **Manager**: Generate reports and approve or reject leave applications and compensation claims.
- **Admin**: Manage employees, job designations, users, roles, leave balances, public holidays and approval hierarchy.

## Login Credentials (username | password)

- Admin
  `admin | adminadmin`
- Manager
  `manager | manager`
- Employee
  `employee | employee`

## Running Locally

### IDE

- Recommended: IntelliJ IDEA Ultimate
- SpringToolSuite4

### Prerequisites

- JDK 17
- Maven
- MySQL Server

### Default Database Credentials (change these in `application.properties`)

- MySQL Username: `root`
- MySQL Password: `root`

### Steps

1. **Create the Schema**
   Start your MySQL server and Run the following SQL command to create the schema:
   ```sql
   CREATE DATABASE leave_processing_system;
   ```

2. **Build the Project**
   ```sh
   mvn clean install
   ```

3. **Run the Application**
   ```sh
   mvn spring-boot:run
   ```

4. **Seed Mock Data**<br>
   The mock data will be seeded through the command-line runner.

5. **Access the Application**<br>
   Open your browser and go to [http://localhost:8080](http://localhost:8080).


## Troubleshooting

- Ensure MySQL server is running and accessible.
- Check for any port conflicts on `8080`.
- Verify the database credentials in `application.properties`.

# Customizing

## Navigation Bar

### Adding

To add the navigation bar to your page, include the following line after the `<body>` tag in your HTML file:

```html

<div th:replace="fragments/navbar :: menu"></div>
```

Replace your `<head></head>` tag in your HTML file with:

```html

<head th:replace="fragments/head :: html_head"></head>
```

This will insert the navigation bar onto the top of your page, along with bootstrap and a title. Ignore the 'Missing
required title element warning by Intellij'.

\
`/home/admin` will look like this with the navbar:

```html
<!DOCTYPE html>
<html lang="en">
<head th:replace="fragments/head :: html_head"></head>

<body>
<div th:replace="fragments/navbar :: menu"></div>

Admin Home
</body>
</html>
```

### Customizing

Customize the navigation bar by editing the `navbar.html` file located at:

```
src/main/resources/templates/fragments/navbar.html
```

This file contains the HTML and Bootstrap classes for the navigation bar.
To change or add more navigation items, edit / add to the relevant sections in the `<ul class="navbar-nav">` tag.

```html
<body>
<!-- Some code omitted -->
<ul class="navbar-nav">
    <!-- Example of home/admin called Home for Admin Only -->
    <li class="nav-item pt-2" th:if="${#authorization.expression('hasRole(''ADMIN'')')}">
        <a class="nav-link" th:href="@{/home/admin}">Home</a>
    </li>

    <!-- Example of home/manager called Home 1 for Manager Only -->
    <li class="nav-item pt-2" th:if="${#authorization.expression('hasRole(''MANAGER'')')}">
        <a class="nav-link" th:href="@{/home/manager}">Home 1</a>
    </li>
</ul>
</body>
```
