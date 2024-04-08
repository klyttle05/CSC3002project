CREATE TABLE EventParticipants (
    participant_id INT AUTO_INCREMENT PRIMARY KEY,
    activity_id INT NOT NULL,
    student_id INT NOT NULL,
    FOREIGN KEY (activity_id) REFERENCES ScheduledActivities(activity_id),
    FOREIGN KEY (student_id) REFERENCES Students(student_id)
);