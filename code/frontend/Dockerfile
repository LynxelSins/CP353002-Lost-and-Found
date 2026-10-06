# Multi-stage build for React Vite Frontend
# Stage 1: Build
FROM node:20-alpine AS builder
WORKDIR /app

# Copy package.json and package-lock.json first for caching
COPY package*.json ./
RUN npm ci

# Declare Build Argument & ENV before copying code (ช่วยให้ Docker Cache ทำงานไวขึ้น)
ARG VITE_API_BASE_URL
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL

# Copy frontend source code
COPY . .

# Build production bundle
RUN npm run build

# Stage 2: Serve with Nginx
FROM nginx:alpine
WORKDIR /usr/share/nginx/html

# Clean default nginx website
RUN rm -rf ./*

# Copy built assets from builder stage
COPY --from=builder /app/dist .

# Copy custom nginx configuration for SPA routing
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]