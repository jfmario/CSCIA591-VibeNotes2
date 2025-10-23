# EC2 Deployment Quick Start Guide

## Step 1: Launch EC2 Instance

1. **Instance Type**: t2.medium or larger
2. **OS**: Ubuntu 22.04 LTS or Amazon Linux 2023
3. **Security Group**: Open ports:
   - 22 (SSH)
   - 80 (HTTP)
   - 443 (HTTPS)
   - 8080 (Backend - optional, use Nginx proxy instead)

## Step 2: Install Dependencies

```bash
# Update system
sudo apt-get update && sudo apt-get upgrade -y

# Install Java 17
sudo apt-get install openjdk-17-jdk -y
java -version

# Install Maven
sudo apt-get install maven -y
mvn -version

# Install Node.js and npm
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs
node -v && npm -v

# Install PostgreSQL
sudo apt-get install postgresql postgresql-contrib -y
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Install Nginx
sudo apt-get install nginx -y
sudo systemctl start nginx
sudo systemctl enable nginx
```

## Step 3: Setup PostgreSQL Database

```bash
sudo -u postgres psql

# In PostgreSQL prompt:
CREATE DATABASE vibenotes2;
CREATE USER vibenotes WITH PASSWORD 'your_secure_password';
GRANT ALL PRIVILEGES ON DATABASE vibenotes2 TO vibenotes;
\q
```

## Step 4: Clone and Configure Application

```bash
# Clone or upload your application
cd /home/ubuntu
# (Upload your VibeNote2 folder)

# Set environment variables
cat > /home/ubuntu/vibenotes-env.sh << 'EOF'
export SERVER_PORT=8080
export DATABASE_URL=jdbc:postgresql://localhost:5432/vibenotes2
export DATABASE_USERNAME=vibenotes
export DATABASE_PASSWORD=your_secure_password
export HIBERNATE_DDL_AUTO=update
export SHOW_SQL=false
export JWT_SECRET=YourVeryLongAndSecureSecretKeyHereThatMustBeAtLeast64CharactersLongForHS512Algorithm
export JWT_EXPIRATION=86400000
export CORS_ALLOWED_ORIGINS=http://your-ec2-ip,http://your-domain.com,https://your-domain.com
export AVATAR_UPLOAD_DIR=/var/vibenotes/uploads/avatars
export ATTACHMENT_UPLOAD_DIR=/var/vibenotes/uploads/attachments
EOF

chmod +x /home/ubuntu/vibenotes-env.sh
source /home/ubuntu/vibenotes-env.sh
```

## Step 5: Build Backend

```bash
cd /home/ubuntu/VibeNote2/backend
source /home/ubuntu/vibenotes-env.sh
mvn clean package -DskipTests

# Create upload directories
sudo mkdir -p /var/vibenotes/uploads/avatars
sudo mkdir -p /var/vibenotes/uploads/attachments
sudo chown -R ubuntu:ubuntu /var/vibenotes
```

## Step 6: Create Backend Service

```bash
sudo nano /etc/systemd/system/vibenotes-backend.service
```

Add the following content:

```ini
[Unit]
Description=VibeNotes Backend Service
After=network.target postgresql.service

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/home/ubuntu/VibeNote2/backend
EnvironmentFile=/home/ubuntu/vibenotes-env.sh
ExecStart=/usr/bin/java -jar /home/ubuntu/VibeNote2/backend/target/vibenotes-1.0.0.jar
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```

Start the service:

```bash
sudo systemctl daemon-reload
sudo systemctl enable vibenotes-backend
sudo systemctl start vibenotes-backend
sudo systemctl status vibenotes-backend
```

## Step 7: Build and Deploy Frontend

```bash
cd /home/ubuntu/VibeNote2/frontend

# For EC2 with public IP
export VUE_APP_API_URL=http://YOUR_EC2_PUBLIC_IP:8080

# For domain name
# export VUE_APP_API_URL=http://api.your-domain.com

npm install
npm run build

# Deploy to Nginx
sudo rm -rf /var/www/html/*
sudo cp -r dist/* /var/www/html/
```

## Step 8: Configure Nginx

```bash
sudo nano /etc/nginx/sites-available/default
```

Replace with:

```nginx
server {
    listen 80;
    server_name your-domain.com;  # or use EC2 public IP
    
    # Frontend
    root /var/www/html;
    index index.html;
    
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    # Backend API Proxy
    location /api {
        proxy_pass http://localhost:8080/api;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_http_version 1.1;
        proxy_set_header Connection "";
    }
    
    # File Uploads Proxy
    location /uploads {
        proxy_pass http://localhost:8080/uploads;
    }
    
    # Increase max body size for file uploads
    client_max_body_size 10M;
}
```

Test and restart Nginx:

```bash
sudo nginx -t
sudo systemctl restart nginx
```

## Step 9: Test the Application

```bash
# Check backend
curl http://localhost:8080/api/auth/test

# Open in browser
http://YOUR_EC2_PUBLIC_IP
```

## Step 10: Setup SSL (Optional but Recommended)

```bash
sudo apt-get install certbot python3-certbot-nginx -y
sudo certbot --nginx -d your-domain.com
```

## Environment Variables Reference

For EC2 deployment, set these environment variables:

| Variable | Description | Example |
|----------|-------------|---------|
| `SERVER_PORT` | Backend port | `8080` |
| `DATABASE_URL` | PostgreSQL connection string | `jdbc:postgresql://localhost:5432/vibenotes2` |
| `DATABASE_USERNAME` | Database username | `vibenotes` |
| `DATABASE_PASSWORD` | Database password | `your_secure_password` |
| `JWT_SECRET` | JWT signing key (64+ chars) | `your_long_secret_key` |
| `CORS_ALLOWED_ORIGINS` | Allowed frontend URLs | `http://domain.com,https://domain.com` |
| `VUE_APP_API_URL` | Frontend API URL | `http://your-ec2-ip:8080` |

## Troubleshooting

### Backend not starting
```bash
sudo journalctl -u vibenotes-backend -f
```

### Database connection issues
```bash
sudo systemctl status postgresql
psql -U vibenotes -d vibenotes2 -h localhost
```

### CORS errors
- Update `CORS_ALLOWED_ORIGINS` to include your frontend URL
- Restart backend after changes

### File upload issues
- Check directory permissions: `ls -la /var/vibenotes/uploads`
- Ensure directories exist and are writable by the service user

## Quick Commands

```bash
# Restart backend
sudo systemctl restart vibenotes-backend

# View logs
sudo journalctl -u vibenotes-backend -f

# Rebuild and redeploy
cd /home/ubuntu/VibeNote2/backend
source /home/ubuntu/vibenotes-env.sh
mvn clean package -DskipTests
sudo systemctl restart vibenotes-backend
```

