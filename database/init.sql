CREATE TABLE IF NOT EXISTS service_releases (
    service_name VARCHAR NOT NULL,
    service_version INTEGER NOT NULL,
    PRIMARY KEY (service_name, service_version)
);

CREATE TABLE IF NOT EXISTS system_release_versions (
    version INTEGER PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS system_releases(
    service_name VARCHAR NOT NULL,
    service_version INTEGER NOT NULL,
    system_release_version INTEGER REFERENCES system_release_versions(version),
    FOREIGN KEY (service_name, service_version) REFERENCES  service_releases(service_name, service_version)
);

CREATE INDEX IF NOT EXISTS service_name_service_version_idx ON system_releases(service_name, service_version);
CREATE INDEX IF NOT EXISTS system_release_version_ids ON system_releases(system_release_version);