# PolicySphere – Policy Service

Spring Boot 3 / Java 21 / MySQL (`policy_db`), port 8082.
Registers in Eureka and calls `customer-service` via OpenFeign (service name, no host/port).

- CRUD: `/api/v1/policies`
- Reports (Stream API): `/api/v1/policies/reports/{summary,by-type,active,expiring}`
- Test data: `scripts/seed-policies.sql`
