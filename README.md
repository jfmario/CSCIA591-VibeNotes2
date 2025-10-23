# VibeNotes

A simple note-taking web application built with Spring Boot and Vue.js.

## Features

- ✅ User Registration
- ✅ User Login with JWT Authentication
- 🎨 Modern, responsive UI

## Tech Stack

### Backend
- Java 17
- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- PostgreSQL Database
- JWT Authentication

### Frontend
- Vue.js 3
- Vue Router
- Axios
- Modern CSS

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Node.js 14+ and npm
- PostgreSQL 12+

## Database Setup

The application connects to a PostgreSQL database with the following configuration:

- **Host:** localhost
- **Port:** 5432
- **Database:** vibenotes2
- **Username:** postgres
- **Password:** password

Make sure PostgreSQL is running and the database `vibenotes2` is created with these credentials.

## Installation & Running

### Backend (Spring Boot)

1. Navigate to the backend directory:
```bash
cd backend
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### Frontend (Vue.js)

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Run the development server:
```bash
npm run serve
```

The frontend will start on `http://localhost:8081`

## Usage

1. Open your browser and navigate to `http://localhost:8081`
2. Click "Register here" to create a new account
3. Enter a username (min 3 characters) and password (min 6 characters)
4. After registration, you'll be automatically logged in
5. Use the same credentials to log in on subsequent visits

## API Endpoints

### Authentication

- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login with credentials
- `GET /api/auth/test` - Test endpoint

## Project Structure

```
VibeNote2/
├── backend/                    # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/vibenotes/
│   │   │   │   ├── config/    # Security configuration
│   │   │   │   ├── controller/# REST controllers
│   │   │   │   ├── dto/       # Data transfer objects
│   │   │   │   ├── model/     # JPA entities
│   │   │   │   ├── repository/# Data repositories
│   │   │   │   ├── security/  # JWT filter
│   │   │   │   ├── service/   # Business logic
│   │   │   │   └── util/      # Utility classes
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   └── pom.xml
├── frontend/                   # Vue.js frontend
│   ├── public/
│   ├── src/
│   │   ├── views/             # Page components
│   │   ├── services/          # API service
│   │   ├── router/            # Vue Router config
│   │   ├── App.vue
│   │   └── main.js
│   ├── package.json
│   └── vue.config.js
└── README.md
```

## Security

The application uses JWT (JSON Web Tokens) for authentication:
- Tokens are generated upon successful login/registration
- Tokens are stored in localStorage
- Protected routes require a valid JWT token
- Passwords are encrypted using BCrypt

## Future Enhancements

- Create, edit, and delete notes
- Note categories and tags
- Rich text editor
- Search functionality
- Note sharing
- Export notes

## License

This project is open source and available for personal and educational use.

