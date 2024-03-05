CREATE TABLE Schedule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    FOREIGN KEY (student_id) REFERENCES Student(id)
);
