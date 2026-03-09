# Whydah-CRMService

## Purpose
A CRM API extension for Whydah that manages customer entities separate from user identities. Provides CRUD operations on customers, customer authentication activity tracking, and search capabilities. Recognizes that "user != customer" and maintains the customer dimension alongside Whydah user identities.

## Tech Stack
- Language: Java 21
- Framework: Ratpack 1.9
- Build: Maven
- Key dependencies: Whydah-Java-SDK, Ratpack, SLF4J

## Architecture
Standalone microservice with a simple persistent customer implementation. Exposes REST APIs for customer CRUD operations, customer search, and authentication activity tracking. Designed to be forked and customized for specific CRM needs. Integrates with the broader Whydah identity ecosystem while maintaining its own customer data store.

## Key Entry Points
- Customer REST API endpoints
- `pom.xml` - Maven coordinates: `net.whydah.service:Whydah-CRMService`

## Development
```bash
# Build
mvn clean install

# Test
mvn test

# Run
java -jar target/Whydah-CRMService-*.jar
```

## Domain Context
Customer relationship management within the Whydah IAM ecosystem. Bridges the gap between identity management (users) and business relationship management (customers), supporting customer lifecycle tracking alongside SSO user identities.
