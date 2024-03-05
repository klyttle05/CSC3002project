CREATE TABLE ScheduledActivity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    room_id BIGINT,
    FOREIGN KEY (room_id) REFERENCES Room(id)
);
