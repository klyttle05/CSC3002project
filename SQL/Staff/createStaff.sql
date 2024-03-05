CREATE TABLE Staff (
    id BIGINT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    department_id BIGINT,
    FOREIGN KEY (department_id) REFERENCES Department(id)
);
