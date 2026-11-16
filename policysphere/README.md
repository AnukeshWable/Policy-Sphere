# PolicySphere

| Project | Port | Role |
|---|---|---|
| discovery-server | 8761 | Eureka registry |
| customer-service | 8081 | Customers, MySQL `customer_db` |
| policy-service | 8082 | Policies + reports, MySQL `policy_db`, calls customer-service via OpenFeign |
| api-gateway | 8080 | Spring Cloud Gateway (reactive/WebFlux), routes via Eureka (`lb://`) |

## One-time DB setup (as MySQL root)
    CREATE DATABASE IF NOT EXISTS customer_db;
    CREATE DATABASE IF NOT EXISTS policy_db;
    CREATE USER IF NOT EXISTS 'policy_app'@'localhost' IDENTIFIED BY 'AppPass123';
    GRANT ALL PRIVILEGES ON customer_db.* TO 'policy_app'@'localhost';
    GRANT ALL PRIVILEGES ON policy_db.*   TO 'policy_app'@'localhost';
    FLUSH PRIVILEGES;

## Start order
1. MySQL  2. discovery-server  3. customer-service  4. policy-service  5. api-gateway
Dashboard: http://localhost:8761. Always call APIs through http://localhost:8080.
