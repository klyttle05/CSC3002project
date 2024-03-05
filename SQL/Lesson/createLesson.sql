CREATE TABLE Lesson (
    id BIGINT PRIMARY KEY,
    title VARCHAR(255),
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    location_id BIGINT,
    module_id BIGINT,
    instructor_id BIGINT,
    FOREIGN KEY (location_id) REFERENCES Room(id),
    FOREIGN KEY (module_id) REFERENCES Module(id),
    FOREIGN KEY (instructor_id) REFERENCES Staff(id)
);
