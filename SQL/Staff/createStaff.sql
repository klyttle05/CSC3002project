CREATE TABLE Staff (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL, -- Added password field
    department_id BIGINT,
    FOREIGN KEY (department_id) REFERENCES Department(id)
);

