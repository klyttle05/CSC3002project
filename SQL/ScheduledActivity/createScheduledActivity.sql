CREATE TABLE ScheduledActivities (
    activity_id INT AUTO_INCREMENT PRIMARY KEY,
    type ENUM('Lesson', 'Exam', 'Event') NOT NULL,
    title VARCHAR(255),
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    location VARCHAR(255),
    module_id INT,
    staff_id INT,
    FOREIGN KEY (module_id) REFERENCES Modules(module_id),
    FOREIGN KEY (staff_id) REFERENCES Staff(staff_id)
);
