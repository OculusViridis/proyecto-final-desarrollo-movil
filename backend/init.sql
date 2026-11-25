CREATE TABLE IF NOT EXISTS usuarios (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    correo VARCHAR(150) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    carnet VARCHAR(50),
    carrera VARCHAR(150),
    creado_en TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
