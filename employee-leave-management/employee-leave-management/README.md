# Employee Leave Management System API

Intermediate Java Developer Internship Task

## Stack

- Java 21
- Spring Boot 4.1.0
- Spring Web
- Spring Data JPA / Hibernate
- MySQL
- Jakarta Bean Validation
- Maven

Spring Boot 4.1.0 is used because it is a current stable Spring Boot release.

## Architecture

Controller -> Service -> Repository -> MySQL

## Main Features

- Employee registration
- Admin/employee roles
- Leave request creation
- Leave history
- Leave status tracking
- Admin approval/rejection
- Employee cancellation of pending requests
- Date validation
- Overlapping leave validation
- Global exception handling
- MySQL persistence

## Setup

### 1. Create the database

Run:

```sql
CREATE DATABASE leave_management;
```

### 2. Configure MySQL

Open:

`src/main/resources/application.properties`

Change:

```properties
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### 3. Run

```bash
mvn clean spring-boot:run
```

Or run `LeaveManagementApplication` from IntelliJ IDEA / Eclipse.

## API Examples

### Create employee

POST `/api/users`

```json
{
  "name": "John Employee",
  "email": "john@example.com",
  "role": "EMPLOYEE"
}
```

### Create admin

POST `/api/users`

```json
{
  "name": "Admin User",
  "email": "admin@example.com",
  "role": "ADMIN"
}
```

### Submit leave

POST `/api/leaves/employee/1`

```json
{
  "startDate": "2026-09-10",
  "endDate": "2026-09-12",
  "reason": "Personal work"
}
```

### View employee history

GET `/api/leaves/employee/1`

### Admin: view all requests

GET `/api/admin/leaves`

### Admin: approve

PUT `/api/admin/leaves/1/approve?adminId=2`

```json
{
  "comment": "Approved. Have a good day."
}
```

### Admin: reject

PUT `/api/admin/leaves/1/reject?adminId=2`

```json
{
  "comment": "Team availability is limited on these dates."
}
```

### Employee: cancel pending request

PUT `/api/leaves/1/cancel?employeeId=1`

## Important Design Notes

This internship implementation deliberately keeps authentication separate from the core leave workflow. The admin/employee role is stored and checked by the service layer.

For a production deployment, add Spring Security with password hashing and JWT/session authentication rather than trusting IDs supplied in request parameters.

## Suggested Demo Flow

1. Create an employee.
2. Create an admin.
3. Employee submits a leave request.
4. Show the request as PENDING.
5. Admin views all requests.
6. Admin approves or rejects it.
7. Employee views updated leave history.
8. Demonstrate validation by submitting an invalid date or overlapping request.

## Internship Requirement Mapping

The project covers employee leave creation, leave history, approval/rejection, employee/admin roles, status tracking, validation, database persistence, REST API design, and layered controller/service/repository/entity architecture.
