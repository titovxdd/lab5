CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(96) NOT NULL,
    permissions VARCHAR(20) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS coordinates (
    id SERIAL PRIMARY KEY,
    x BIGINT NOT NULL,
    y REAL NOT NULL
);

CREATE TABLE IF NOT EXISTS person (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    birthday DATE NOT NULL,
    eye_color VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS music_bands (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    coordinates_id INTEGER NOT NULL REFERENCES coordinates(id) ON DELETE CASCADE,
    creation_date DATE NOT NULL,
    number_of_participants BIGINT NOT NULL CHECK (number_of_participants > 0),
    singles_count BIGINT NOT NULL CHECK (singles_count > 0),
    description VARCHAR(255),
    genre VARCHAR(50),
    front_man_id INTEGER NOT NULL REFERENCES person(id) ON DELETE CASCADE,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT NOW()
);