CREATE TABLE Exam (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    duration_minutes INT,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    location_id BIGINT,
    module_id BIGINT,
    FOREIGN KEY (location_id) REFERENCES Room(id),
    FOREIGN KEY (module_id) REFERENCES Module(id)
);
