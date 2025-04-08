
CREATE TABLE IF NOT EXISTS users (
    users_id UUID PRIMARY KEY,
    users_email TEXT NOT NULL,
    users_password TEXT NOT NULL,
    users_screen_name TEXT,
    users_created_at TIMESTAMP WITH TIME ZONE DEFAULT current_timestamp,
    users_updated_at TIMESTAMP WITH TIME ZONE DEFAULT current_timestamp
);

CREATE UNIQUE INDEX users_email_idx ON users (users_email);

