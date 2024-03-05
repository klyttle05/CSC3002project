CREATE TABLE Module (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    course_id BIGINT,
    FOREIGN KEY (course_id) REFERENCES Course(id)
);
