# VibeNotes Quick Start Guide

## Step-by-Step Instructions

### 1. Verify Prerequisites

Make sure you have:
- ✅ Java 17+ installed (`java -version`)
- ✅ Maven installed (`mvn -version`)
- ✅ Node.js and npm installed (`node -version` and `npm -version`)
- ✅ PostgreSQL running on localhost:5432 with database: vibenotes2, username: postgres, password: password

### 2. Start the Backend

Open a terminal and run:

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

You should see:
- Spring Boot starting up
- Connection to MySQL database
- Server running on port 8080
- Tables being created automatically (users table)

### 3. Start the Frontend

Open a **new terminal** window and run:

```bash
cd frontend
npm install
npm run serve
```

You should see:
- Dependencies being installed
- Webpack dev server starting
- App running on http://localhost:8081

### 4. Test the Application

1. Open your browser to **http://localhost:8081**
2. Click "Register here"
3. Create an account:
	- Username: testuser (min 3 characters)
	- Password: password123 (min 6 characters)
	- Confirm Password: password123
4. Click "Register"
5. You should be automatically logged in and see the Dashboard
6. Try logging out and logging back in

### 5. Verify Database

You can check PostgreSQL to see the created user:

```sql
\c vibenotes2
SELECT * FROM users;
```

## Troubleshooting

### Backend won't start
- **Error:** Cannot connect to database
	- **Solution:** Check PostgreSQL is running and credentials are correct. Make sure the database `vibenotes2` exists.
- **Error:** Port 8080 already in use
	- **Solution:** Kill the process using port 8080 or change the port in `application.properties`

### Frontend won't start
- **Error:** Port 8081 already in use
	- **Solution:** Change the port in `vue.config.js`
- **Error:** Module not found
	- **Solution:** Delete `node_modules` and `package-lock.json`, then run `npm install` again

### Login/Register not working
- Check browser console for errors
- Verify backend is running on http://localhost:8080
- Check network tab in browser dev tools
- Verify CORS is properly configured

## What's Next?

Now that your basic authentication is working, you can extend the application with:
- Note creation and management
- Note categories
- Rich text editing
- Search functionality
- And much more!

## API Testing

You can test the backend API directly using curl:

**Register:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
	-H "Content-Type: application/json" \
	-d '{"username":"testuser","password":"password123"}'
```

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
	-H "Content-Type: application/json" \
	-d '{"username":"testuser","password":"password123"}'
```

**Test Endpoint:**
```bash
curl http://localhost:8080/api/auth/test
```

