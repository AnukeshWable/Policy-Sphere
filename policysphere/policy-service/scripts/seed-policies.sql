-- Dev-only test data for the reporting APIs: 10,000 policies, inserted directly into policy_db.
-- This bypasses Customer Service validation on purpose (customer_id values are fake).
USE policy_db;

SET SESSION cte_max_recursion_depth = 20000;

INSERT INTO policies
    (policy_number, customer_id, policy_type, premium_amount, start_date, end_date, status, created_at, updated_at)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 10000
)
SELECT
    CONCAT('SEED-', LPAD(n, 6, '0')),
    1 + (n % 50),
    ELT(1 + (n % 5), 'HEALTH', 'LIFE', 'AUTO', 'HOME', 'TRAVEL'),
    ROUND(5000 + ((n * 37) % 20000), 2),
    DATE_SUB(CURDATE(), INTERVAL (n % 365) DAY),
    DATE_ADD(DATE_SUB(CURDATE(), INTERVAL (n % 365) DAY), INTERVAL 365 DAY),
    IF(n % 4 = 0, 'CANCELLED', 'ACTIVE'),
    NOW(),
    NOW()
FROM seq;

-- Cross-check the API output against plain SQL:
-- SELECT status, COUNT(*), SUM(premium_amount), ROUND(AVG(premium_amount), 2) FROM policies GROUP BY status;
-- SELECT policy_type, COUNT(*) FROM policies GROUP BY policy_type ORDER BY COUNT(*) DESC;
-- SELECT COUNT(*) FROM policies WHERE status = 'ACTIVE' AND end_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY);

-- Cleanup when done:
-- DELETE FROM policies WHERE policy_number LIKE 'SEED-%';
