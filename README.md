# Veridion Monorepo

A distributed web scraping platform for extracting company information from websites. This monorepo contains multiple microservices that work together to scrape company data, store it in a database, and provide search capabilities through a REST API.

## Architecture Overview

The system consists of the following components:

- **API Service** - Spring Boot REST API for managing scrape requests and searching company data
- **Web Scraper** - Node.js worker service that scrapes company websites for contact information
- **Queue Setup** - Node.js service that initializes RabbitMQ queues and exchanges
- **PostgreSQL** - Database for storing company data and scrape jobs
- **Elasticsearch** - Search engine for fast company data retrieval
- **RabbitMQ** - Message broker for coordinating scrape jobs between API and workers

## Components

### 1. API Service (`/api`)

**Technology**: Java 17 + Spring Boot 3.4.6

A REST API service that provides endpoints for:
- Creating and managing scrape jobs
- Searching company data using Elasticsearch
- Storing scraped company information

**Key Features**:
- RESTful API with HATEOAS support
- PostgreSQL integration with JPA/Hibernate
- Elasticsearch integration for search functionality
- RabbitMQ integration for job queuing
- Swagger/OpenAPI documentation
- Database migrations with Flyway

**Key Dependencies**:
- Spring Boot (Web, Data JPA, AMQP, Elasticsearch)
- PostgreSQL driver
- Elasticsearch client
- SpringDoc OpenAPI (Swagger UI)
- MapStruct for mapping
- Lombok for boilerplate reduction

### 2. Web Scraper (`/web-scraper`)

**Technology**: Node.js + Typescript

A worker service that processes scrape requests from the message queue and extracts company information from websites.

**Extracted Data**:
- Phone numbers (using regex pattern matching)
- Social media links (Facebook, Instagram, LinkedIn, Twitter/X, YouTube, TikTok)
- Physical addresses (US format addresses)
- Website URLs

**Key Features**:
- Processes multiple pages per domain (main page + contact page if available)
- Retry mechanism with dead letter queue
- Configurable worker replicas (5 replicas by default)
- Robust error handling and logging

**To be improved**:
- Detect sites that use client-side rendering technologies (React, Angular, etc)
- Use a headless browser for client-side rendered websites
- Use better filters to find phone numbers

### 3. Queue Setup (`/queue-setup`)

**Technology**: Node.js

A utility service that initializes the RabbitMQ infrastructure required for the scraping workflow.

**Created Resources**:
- Exchanges: `scrape_exchange` (direct)
- Queues:
  - `scrape_requests` - Incoming scrape jobs
  - `scrape_results` - Completed scrape results
  - `scrape_dead_letter` - Failed jobs after retry limit
- Bindings with appropriate routing keys

### 4. Supporting Services

- **PostgreSQL 16** - Primary database for relational data storage
- **Elasticsearch 9.0.1** - Search engine for company data indexing
- **RabbitMQ 4.1** - Message broker with management UI enabled

## Running Locally with Docker Compose

### Prerequisites

- Docker and Docker Compose installed
- At least 4GB of available RAM
- Ports 8080, 5672, 15672, 5432, and 9200 available

### Quick Start

1. Clone the repository:
```bash
git clone <repository-url>
cd veridon
```

2. Start all services:
```bash
docker-compose up -d
```

3. Wait for all services to be healthy (this may take 2-3 minutes):
```bash
docker-compose ps
```

4. The services will be available at:
   - **API**: http://localhost:8080
   - **Swagger UI**: http://localhost:8080/swagger-ui/index.html
   - **RabbitMQ Management**: http://localhost:15672 (guest/guest)
   - **Elasticsearch**: http://localhost:9200
   - **PostgreSQL**: localhost:5432 (postgres/password)

### Service Startup Order

The Docker Compose configuration ensures proper startup dependencies:

1. **Infrastructure services** start first: PostgreSQL, Elasticsearch, RabbitMQ
2. **Queue Setup** runs once to initialize RabbitMQ queues
3. **API** and **Web Scraper** services start after dependencies are ready
4. **Web Scrapers** scale to 5 replicas automatically

### API Documentation (Swagger UI)

The API includes comprehensive OpenAPI/Swagger documentation accessible at:

**http://localhost:8080/swagger-ui/index.html**

The Swagger UI provides:
- Interactive API documentation
- Request/response schemas
- Try-it-out functionality for all endpoints
- Authentication details (if applicable)

Key API endpoints include:
- `POST /api/scrape` - Submit scrape requests
- `POST /api/scrape/batch` - Submit batch scrape requests  
- `GET /api/scrape/batches/{id}` - Get scrape batch status
- `POST /api/search` - Search company data

### Environment Configuration

Key environment variables (with defaults):

#### API Service
- `DB_URL` - PostgreSQL connection URL
- `ELASTIC_SEARCH_URL` - Elasticsearch URL
- `RABBITMQ_HOST` - RabbitMQ hostname

#### Web Scraper
- `SCRAPE_EXCHANGE` - RabbitMQ exchange name
- `SCRAPE_REQUESTS_QUEUE` - Queue for incoming requests
- `SCRAPE_RESULT_ROUTING_KEY` - Routing key for results

### Logs and Monitoring

View logs for specific services:
```bash
# API logs
docker-compose logs -f api

# Web scraper logs
docker-compose logs -f web-scraper

# All services
docker-compose logs -f
```

Monitor RabbitMQ queues and messages via the management interface at http://localhost:15672

### Development Workflow

1. **Make code changes** in the respective service directories
2. **Rebuild specific service**:
   ```bash
   docker-compose build api  # or web-scraper, queue-setup
   ```
3. **Restart service**:
   ```bash
   docker-compose up -d api
   ```

### Stopping Services

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (clears data)
docker-compose down -v
```

## API Usage Examples

### Submit a Scrape Job Request
```bash
curl -X POST http://localhost:8080/api/scrape/jobs \
  -H "Content-Type: application/json" \
  -d '{"domain": "example.com"}'
```

### Submit a Scrape Batch Request
```bash
curl -X POST http://localhost:8080/api/scrape/batches \
  -H "Content-Type: application/json" \
  -d '{"domains": ["example.com"]}'
```

### Search Companies
```bash
curl -X POST http://localhost:8080/api/search \
  -H "Content-Type: application/json" \
  -d '{"name": "example company"}'
```

For complete API documentation and interactive testing, visit the Swagger UI at http://localhost:8080/swagger-ui/index.html after starting the services.
