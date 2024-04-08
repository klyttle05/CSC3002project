CREATE TABLE Modules (
    module_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    staff_id INT,
    FOREIGN KEY (staff_id) REFERENCES Staff(staff_id)
);
