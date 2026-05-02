-- Add OAuth2 fields to users table
ALTER TABLE users ADD COLUMN email TEXT;
ALTER TABLE users ADD COLUMN full_name TEXT;
ALTER TABLE users ADD COLUMN picture_url TEXT;
ALTER TABLE users ADD COLUMN provider TEXT;
ALTER TABLE users ADD COLUMN provider_id TEXT;

CREATE UNIQUE INDEX IF NOT EXISTS idx_users_email ON users(email);
