-- Create users table with all required fields (H2 compatible)
CREATE TABLE users (
    id BIGINT IDENTITY PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    email_verification_token VARCHAR(255),
    email_verification_token_expiry TIMESTAMP,
    password_reset_token VARCHAR(255),
    password_reset_token_expiry TIMESTAMP,
    last_login_attempt TIMESTAMP,
    failed_login_attempts INTEGER NOT NULL DEFAULT 0,
    account_locked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX idx_email ON users(email);
CREATE INDEX idx_email_verification_token ON users(email_verification_token);
CREATE INDEX idx_password_reset_token ON users(password_reset_token);

-- Add constraints
ALTER TABLE users ADD CONSTRAINT chk_role 
    CHECK (role IN ('COACH', 'RUNNER', 'ADMIN'));
