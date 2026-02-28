CREATE TABLE IF NOT EXISTS service_releases (
    id SERIAL PRIMARY KEY,
    service_name VARCHAR(100) NOT NULL,
    service_version INTEGER NOT NULL,
    UNIQUE (service_name, service_version)
);

CREATE TABLE IF NOT EXISTS system_release_versions (
    version INTEGER PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS system_releases(
    service_release_id INTEGER REFERENCES service_releases(id),
    system_release_id INTEGER REFERENCES system_release_versions(version),
    UNIQUE (service_release_id, system_release_id)
);