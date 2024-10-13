
# charity-management-system

The Charity Management System is designed to facilitate the creation, management, and donation to various charity cases. It offers functionalities for both case creators and donors, enabling users to manage their charity cases, view donations, and track progress in an organized way.


## Features

- **User Registration and Authentication**: Secure user registration and login with JWT-based authentication.
- **User Profiles**: Each user has a profile displaying their cases and donations.
- **Case Management**: Users can create, update, and delete charity cases.
- **Donation System**: Users can make donations to charity cases.
- **Integration with Google Drive**: Utilizes Google Drive for storing images associated with charity cases.



## Tech Stack

**Client:** Angular

**Server:** Java 17, Spring Boot, Spring Security 6.1

**Authentication**: JWT (JSON Web Tokens)

**Testing FrameWork**: Junit5

**Database:** Postgresql

**Dependency management:** maven



## Prerequisites

- Java 11 or higher
- Node.js and npm
- PostgreSQL
- Google Cloud account for Google Drive API access


## Installation

Clone the repository

```bash
  git clone https://github.com/mukaima/charity-mangament-system.git
```
Navigate into the project directory:

```bash
  cd charity-mangament-system
```  
Install dependencies and build the project:

```bash
  ./mvnw clean install
```
Run the application:

```bash
  ./mvnw spring-boot:run
```
