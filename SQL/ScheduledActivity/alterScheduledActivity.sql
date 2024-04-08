ALTER TABLE ScheduledActivities
ADD COLUMN room_id INT,
ADD FOREIGN KEY (room_id) REFERENCES Rooms(room_id);
