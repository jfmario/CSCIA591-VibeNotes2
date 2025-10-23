#!/bin/bash

# VibeNotes Backend Deployment Script
# This script builds and deploys the backend application

echo "========================================="
echo "VibeNotes Backend Deployment"
echo "========================================="

# Build the application
echo "Building application..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi

echo "Build successful!"

# Create upload directories
echo "Creating upload directories..."
mkdir -p ${AVATAR_UPLOAD_DIR:-uploads/avatars}
mkdir -p ${ATTACHMENT_UPLOAD_DIR:-uploads/attachments}

echo "========================================="
echo "Deployment complete!"
echo "To run the application:"
echo "  java -jar target/vibenotes-1.0.0.jar"
echo "========================================="

