CREATE TABLE Schedule (
    id BIGINT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    FOREIGN KEY (student_id) REFERENCES Student(id)
);
