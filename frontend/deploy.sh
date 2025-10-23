#!/bin/bash

# VibeNotes Frontend Deployment Script
# This script builds the frontend application

echo "========================================="
echo "VibeNotes Frontend Deployment"
echo "========================================="

# Check if VUE_APP_API_URL is set
if [ -z "$VUE_APP_API_URL" ]; then
    echo "WARNING: VUE_APP_API_URL is not set!"
    echo "Using default localhost:8080"
    echo "For production, set: export VUE_APP_API_URL=http://your-backend-url:8080"
fi

# Install dependencies
echo "Installing dependencies..."
npm install

if [ $? -ne 0 ]; then
    echo "Dependency installation failed!"
    exit 1
fi

# Build the application
echo "Building application..."
npm run build

if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi

echo "========================================="
echo "Build complete!"
echo "Files are in: dist/"
echo "Deploy these files to your web server (Nginx/Apache)"
echo "========================================="

