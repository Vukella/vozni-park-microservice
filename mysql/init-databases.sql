-- ─────────────────────────────────────────────────────────────────────────────
-- Vozni Park — microservice database initialisation
-- Runs once on first container start (when the data volume is empty).
-- vozni_park_db is created by MYSQL_DATABASE in docker-compose.yml.
-- This script creates the six additional schemas for the microservices
-- and grants the application user full access to each of them.
-- ─────────────────────────────────────────────────────────────────────────────

CREATE DATABASE IF NOT EXISTS fleet_service_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS travel_order_service_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS technical_maintenance_service_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS document_reporting_service_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS fuel_tracking_service_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Grant the application user access to every microservice schema.
-- root already has full access; this covers vozni_user.
GRANT ALL PRIVILEGES ON fleet_service_db.*                  TO 'vozni_user'@'%';
GRANT ALL PRIVILEGES ON travel_order_service_db.*           TO 'vozni_user'@'%';
GRANT ALL PRIVILEGES ON technical_maintenance_service_db.*  TO 'vozni_user'@'%';
GRANT ALL PRIVILEGES ON document_reporting_service_db.*     TO 'vozni_user'@'%';
GRANT ALL PRIVILEGES ON fuel_tracking_service_db.*          TO 'vozni_user'@'%';

FLUSH PRIVILEGES;