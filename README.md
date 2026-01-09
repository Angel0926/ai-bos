# AI-BOS (AI Business Operating System) — skeleton

Short: This repository is the starter skeleton for AI-BOS — flagship project.
Contains: architecture, docker-compose, AI service stub (FastAPI), users-service placeholder.

Quick start (local):
1. docker-compose up --build
2. open http://localhost:8000/health for AI service

## Tech Stack
- Java 17
- Spring Boot 3
- Spring Security
- PostgreSQL
- JWT (access + refresh)

## Endpoints

POST /api/v1/auth/register  
POST /api/v1/auth/login  
POST /api/v1/auth/refresh  
POST /api/v1/auth/signout

GET /api/v1/auth/me  
GET /api/v1/auth/admin-only


# Users Service

## Features
- User registration
- JWT authentication
- Refresh token rotation
- Logout with token revocation
- Role-based access control

## Auth Flow
1. /register
2. /login → access + refresh
3. access → protected endpoints
4. /refresh → new access token
5. /signout → refresh token invalidated

## Security
- Passwords hashed with BCrypt
- Stateless access tokens (JWT)
- Stateful refresh tokens stored in DB

