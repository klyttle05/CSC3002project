CREATE TABLE Course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    code VARCHAR(50),
    description TEXT,
    department_id BIGINT,
    FOREIGN KEY (department_id) REFERENCES Department(id)
);
