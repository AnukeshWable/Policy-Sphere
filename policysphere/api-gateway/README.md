# PolicySphere – API Gateway
Spring Cloud Gateway (WebFlux), port 8080, Eureka-aware.
Routes (by service name, via Eureka):
- /api/v1/customers/** -> lb://customer-service
- /api/v1/policies/**  -> lb://policy-service
