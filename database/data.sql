-- Optional reference seed data.
-- The application already seeds this same baseline data automatically on first boot
-- via DataInitializer.java, so running this file manually is only necessary if you
-- disabled that initializer or want to reset the database independently of the app.
-- Passwords below are BCrypt hashes; the plaintext values are documented in the README
-- under "Demo credentials" (Admin@123, Manager@123, Employee@123).

USE leave_management_db;

INSERT INTO departments (name, description, created_at, updated_at) VALUES
    ('Engineering', 'Product engineering and platform teams', NOW(), NOW()),
    ('Human Resources', 'People operations and recruitment', NOW(), NOW()),
    ('Sales', 'Sales and business development', NOW(), NOW());

INSERT INTO leave_types (name, description, default_days, is_active) VALUES
    ('Annual Leave', 'Planned yearly vacation leave', 20, TRUE),
    ('Sick Leave', 'Medical and health-related leave', 10, TRUE),
    ('Casual Leave', 'Short-notice personal leave', 7, TRUE);

-- NOTE: user passwords are BCrypt-hashed and cannot be safely hardcoded here without
-- regenerating a hash at insert time. Use the running application's /api/auth/register
-- endpoint, or let DataInitializer.java seed the three demo accounts automatically.
