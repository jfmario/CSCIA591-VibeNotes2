# VibeNotes Deployment Guide

## EC2 Deployment Instructions

### Prerequisites

- EC2 instance running Ubuntu/Amazon Linux
- Java 17+ installed
- PostgreSQL installed or RDS instance configured
- Node.js 14+ and npm installed
- Domain name (optional but recommended)

## Backend Deployment

### 1. Prepare Environment Variables

Create a `.env` file or set environment variables:

```bash
export SERVER_PORT=8080
export DATABASE_URL=jdbc:postgresql://your-db-host:5432/vibenotes2
export DATABASE_USERNAME=your_db_user
export DATABASE_PASSWORD=your_secure_password
export HIBERNATE_DDL_AUTO=update
export SHOW_SQL=false
export JWT_SECRET=YourVeryLongAndSecureSecretKeyHereThatIsAtLeast64CharactersLong123
export JWT_EXPIRATION=86400000
export CORS_ALLOWED_ORIGINS=http://your-domain.com,https://your-domain.com
export AVATAR_UPLOAD_DIR=/var/vibenotes/uploads/avatars
export ATTACHMENT_UPLOAD_DIR=/var/vibenotes/uploads/attachments
```

### 2. Build the Backend

```bash
cd backend
mvn clean package -DskipTests
```

This creates a JAR file in `target/vibenotes-1.0.0.jar`

### 3. Run the Backend

```bash
# Create upload directories
mkdir -p /var/vibenotes/uploads/avatars
mkdir -p /var/vibenotes/uploads/attachments

# Run the application
java -jar target/vibenotes-1.0.0.jar
```

### 4. Run as a Service (Recommended)

Create `/etc/systemd/system/vibenotes-backend.service`:

```ini
[Unit]
Description=VibeNotes Backend Service
After=network.target

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/home/ubuntu/vibenotes/backend
Environment="SERVER_PORT=8080"
Environment="DATABASE_URL=jdbc:postgresql://localhost:5432/vibenotes2"
Environment="DATABASE_USERNAME=postgres"
Environment="DATABASE_PASSWORD=your_password"
Environment="JWT_SECRET=YourVeryLongAndSecureSecretKeyHere"
Environment="CORS_ALLOWED_ORIGINS=http://your-domain.com,https://your-domain.com"
ExecStart=/usr/bin/java -jar /home/ubuntu/vibenotes/backend/target/vibenotes-1.0.0.jar
Restart=always

[Install]
WantedBy=multi-user.target
```

Enable and start the service:

```bash
sudo systemctl daemon-reload
sudo systemctl enable vibenotes-backend
sudo systemctl start vibenotes-backend
sudo systemctl status vibenotes-backend
```

## Frontend Deployment

### 1. Configure API URL

Create `.env.production` file:

```bash
VUE_APP_API_URL=http://your-ec2-public-ip:8080
# or
VUE_APP_API_URL=https://api.your-domain.com
```

### 2. Build the Frontend

```bash
cd frontend
npm install
npm run build
```

This creates optimized files in the `dist/` directory.

### 3. Serve with Nginx

Install Nginx:

```bash
sudo apt-get update
sudo apt-get install nginx
```

Create Nginx configuration `/etc/nginx/sites-available/vibenotes`:

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    root /var/www/vibenotes;
    index index.html;
    
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    # Optional: Proxy backend through Nginx
    location /api {
        proxy_pass http://localhost:8080/api;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
    
    location /uploads {
        proxy_pass http://localhost:8080/uploads;
    }
}
```

Enable the site:

```bash
sudo ln -s /etc/nginx/sites-available/vibenotes /etc/nginx/sites-enabled/
sudo cp -r frontend/dist/* /var/www/vibenotes/
sudo nginx -t
sudo systemctl restart nginx
```

## Database Setup

### Using RDS PostgreSQL

1. Create an RDS PostgreSQL instance
2. Note the endpoint URL
3. Update `DATABASE_URL` environment variable
4. Ensure security group allows connection from EC2

### Using Local PostgreSQL

```bash
sudo apt-get install postgresql postgresql-contrib
sudo -u postgres psql

CREATE DATABASE vibenotes2;
CREATE USER vibenotes WITH PASSWORD 'your_secure_password';
GRANT ALL PRIVILEGES ON DATABASE vibenotes2 TO vibenotes;
\q
```

## Security Considerations

1. **Change JWT Secret**: Use a strong, random secret in production
2. **HTTPS**: Set up SSL certificate (use Let's Encrypt/Certbot)
3. **Firewall**: Configure security groups to allow only necessary ports
4. **Database**: Use strong passwords and restrict access
5. **File Uploads**: Consider using S3 for file storage in production
6. **Environment Variables**: Never commit `.env` files to Git

## SSL/HTTPS Setup (Recommended)

```bash
sudo apt-get install certbot python3-certbot-nginx
sudo certbot --nginx -d your-domain.com
```

## Monitoring and Logs

### Backend Logs
```bash
sudo journalctl -u vibenotes-backend -f
```

### Nginx Logs
```bash
sudo tail -f /var/log/nginx/access.log
sudo tail -f /var/log/nginx/error.log
```

## Environment-Specific Configuration

### Development
- Uses localhost URLs
- SQL logging enabled
- Hibernate auto-update schema

### Production
- Uses domain/IP URLs
- SQL logging disabled
- Validate schema (change `HIBERNATE_DDL_AUTO=validate` after first run)
- Use strong JWT secret
- Enable HTTPS

## Scaling Considerations

1. **File Storage**: Use AWS S3 instead of local storage
2. **Database**: Use RDS with automated backups
3. **Load Balancer**: Add ELB for multiple instances
4. **CDN**: Use CloudFront for static assets
5. **Caching**: Add Redis for session management

## Quick Deployment Script

```bash
#!/bin/bash

# Build backend
cd backend
mvn clean package -DskipTests

# Build frontend
cd ../frontend
npm install
npm run build

# Deploy
sudo systemctl stop vibenotes-backend
sudo cp backend/target/vibenotes-1.0.0.jar /opt/vibenotes/
sudo rm -rf /var/www/vibenotes/*
sudo cp -r frontend/dist/* /var/www/vibenotes/
sudo systemctl start vibenotes-backend
sudo systemctl restart nginx

echo "Deployment complete!"
```

## Troubleshooting

### Backend won't start
- Check database connectivity
- Verify environment variables
- Check logs: `sudo journalctl -u vibenotes-backend`

### CORS errors
- Verify `CORS_ALLOWED_ORIGINS` includes your frontend URL
- Check that protocol (http/https) matches

### File uploads fail
- Check directory permissions
- Verify upload directories exist
- Check disk space

### Database connection fails
- Verify PostgreSQL is running
- Check firewall rules
- Verify credentials

## Backup Strategy

```bash
# Database backup
pg_dump vibenotes2 > backup_$(date +%Y%m%d).sql

# Files backup
tar -czf uploads_backup_$(date +%Y%m%d).tar.gz /var/vibenotes/uploads/
```

