CREATE TABLE customer(
    id BIGSERIAL,
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    age INT NOT NULL,
    primary key (id)
);