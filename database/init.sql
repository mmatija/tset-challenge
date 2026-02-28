CREATE TABLE IF NOT EXISTS service_releases (
    service_name VARCHAR(100),
    service_version INTEGER,
    PRIMARY KEY (service_name, service_version)
);

