# Requirements, Architecture and Technology Setup

## Project
CI/CD Pipeline for a Gym Membership Portal

## 1. Functional Requirements

### FR1 – Create Member
The system shall allow an authorized gym staff member to create a new member record.

Member information will include:
- Member ID
- Name
- Email
- Phone Number
- Membership Plan
- Membership Start Date
- Membership End Date
- Membership Status

### FR2 – View Members
The system shall display registered gym members in a structured list.

### FR3 – Update Member
Authorized staff shall be able to update existing member information.

### FR4 – Search Members
The system shall allow staff to search members using their name or member ID.

### FR5 – Role-Based Status Workflow
The system shall allow authorized users to manage membership status.

Possible statuses:
- Active
- Inactive
- Expired

### FR6 – Summary Dashboard
The system shall provide a dashboard showing:
- Total Members
- Active Members
- Inactive Members
- Expired Members

### FR7 – Authentication
The system shall provide basic login functionality for authorized users.

## 2. Non-Functional Requirements

### Performance
The application should respond quickly for normal gym operations.

### Security
Only authorized users should be able to modify member information.

### Usability
The interface should be simple and easy for gym staff to understand.

### Reliability
Member information should be stored consistently without accidental data loss.

### Maintainability
The application should use a modular structure so that new features can be added easily.

### Testability
The application should support automated testing through the CI/CD pipeline.

## 3. User Roles

### Admin
- Login
- View dashboard
- Create members
- View members
- Update members
- Search members
- Change membership status

### Gym Staff
- Login
- Create members
- View members
- Update members
- Search members

## 4. Technology Stack

### Frontend
HTML5
CSS3
JavaScript
Bootstrap

### Backend
Java
Spring Boot

### Build Tool
Apache Maven

### Database
MySQL

### Version Control
Git
GitHub

### CI/CD
Jenkins

### Testing
JUnit
Selenium

### Containerization
Docker

### Deployment
Apache Tomcat / Docker-based deployment

### Development Environment
Visual Studio Code or IntelliJ IDEA
Java JDK
Maven
Git
MySQL
Docker
Jenkins

## 5. Proposed System Architecture

The system will follow a layered architecture:

User
  ↓
Web Browser
  ↓
Frontend
(HTML + CSS + JavaScript)
  ↓
Spring Boot Application
  ↓
REST/API Layer
  ↓
Service Layer
  ↓
Repository/Data Access Layer
  ↓
MySQL Database

CI/CD:

Developer
  ↓
GitHub
  ↓
Jenkins
  ↓
Build
  ↓
Automated Tests
  ↓
Docker Image
  ↓
Deployment
  ↓
Running Gym Membership Portal

## 6. Main Application Components

### Presentation Layer
Provides the web interface used by gym administrators and staff.

### Controller/API Layer
Receives requests from the frontend and sends appropriate responses.

### Service Layer
Contains the main business logic of the application.

### Repository Layer
Handles communication with the database.

### Database
Stores member information and membership status.

## 7. Main Data Model

### Member

Member ID
Name
Email
Phone
Membership Plan
Start Date
End Date
Status

### User

User ID
Username
Password
Role

## 8. Main API Endpoints

GET    /api/members
GET    /api/members/{id}
POST   /api/members
PUT    /api/members/{id}
DELETE /api/members/{id}
GET    /api/members/search
PUT    /api/members/{id}/status

## 9. Success Criteria

The architecture and setup will be considered successful when:

1. The application can run locally.
2. A member can be created.
3. Members can be viewed.
4. Member information can be updated.
5. Members can be searched.
6. Membership status can be changed.
7. Dashboard statistics can be displayed.
8. Data can be stored in the database.
9. The project can be built using Maven.
10. The application can be tested automatically.
11. The application can be integrated with Jenkins.
12. The application can be containerized using Docker.

## 10. Local Development Setup

Required software:

1. Java JDK
2. Apache Maven
3. Git
4. MySQL
5. Docker
6. Jenkins
7. Visual Studio Code or IntelliJ IDEA

The development environment will be configured before application development begins.

## 11. Implementation Notes

Student: AMANKUMAR JHA  
Roll No: 23102C0072

The implemented application uses the Spring Boot layered structure at `src/main/java/edu/college/gymportal`: MVC and REST controllers, `MemberService`, `MemberRepository`, `Member`, plan/status enums, and `SecurityConfig`. Thymeleaf templates and CSS are under `src/main/resources`. Tests use H2; deployed data uses MySQL 8.4 through Docker Compose. The API currently uses `GET /api/members?search=...` for name, email, or numeric ID search. Membership status updates are restricted to admins.

Local accounts are `admin` and `staff`; passwords must be supplied through `GYM_ADMIN_PASSWORD` and `GYM_STAFF_PASSWORD`. The demo uses in-memory BCrypt-encoded user definitions and Hibernate schema updates. These choices keep the lab setup compact; a production service would use persistent user accounts and versioned schema migrations.

See `Architecture.md`, `Database.md`, `API_Documentation.md`, and `Testing.md` for implementation diagrams and endpoint/test details.
