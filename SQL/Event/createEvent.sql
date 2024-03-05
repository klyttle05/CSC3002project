CREATE TABLE Event (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    description TEXT,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    location_id BIGINT,
    FOREIGN KEY (location_id) REFERENCES Room(id)
);
