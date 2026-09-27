INSERT INTO roles (name) VALUES 
('ROLE_ADMIN'), 
('ROLE_MANAGER'), 
('ROLE_EMPLOYEE');

INSERT INTO departments (name, code, description) VALUES
('Engineering', 'ENG', 'Engineering Department'),
('Human Resources', 'HR', 'Human Resources Department'),
('Finance', 'FIN', 'Finance Department'),
('Operations', 'OPS', 'Operations Department'),
('Marketing', 'MKT', 'Marketing Department');

INSERT INTO routes (name, source, destination, distance_km, estimated_duration_mins) VALUES
('North City Route', 'City Center', 'North Office Park', 15.5, 45),
('South Suburb Route', 'South Station', 'Main Campus', 22.0, 60);

INSERT INTO route_stops (route_id, stop_name, stop_order, latitude, longitude) VALUES
((SELECT id FROM routes WHERE name = 'North City Route'), 'City Center Mall', 1, 40.7128, -74.0060),
((SELECT id FROM routes WHERE name = 'North City Route'), 'North Square', 2, 40.7200, -74.0100),
((SELECT id FROM routes WHERE name = 'North City Route'), 'North Office Park', 3, 40.7300, -74.0200),
((SELECT id FROM routes WHERE name = 'South Suburb Route'), 'South Station', 1, 40.6900, -73.9800),
((SELECT id FROM routes WHERE name = 'South Suburb Route'), 'River Road', 2, 40.7000, -73.9900),
((SELECT id FROM routes WHERE name = 'South Suburb Route'), 'Main Campus', 3, 40.7100, -74.0000);
