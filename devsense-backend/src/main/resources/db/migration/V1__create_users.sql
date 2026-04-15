-- V1__create_users.sql
-- This file runs ONCE on first startup, never again

-- Enable extensions (PostgreSQL add-ons)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
-- uuid-ossp: generates UUIDs with gen_random_uuid()

CREATE EXTENSION IF NOT EXISTS "vector";
-- pgvector: stores AI embedding vectors for RAG (Week 5)
-- We enable it now so Flyway doesn't fail later when V3 needs it

-- Users table
CREATE TABLE users (
                       id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    -- gen_random_uuid() auto-generates a UUID like: 550e8400-e29b-41d4-a716-446655440000

                       email         VARCHAR(255) NOT NULL UNIQUE,
    -- NOT NULL = required, UNIQUE = no two users can have same email

                       password      VARCHAR(255) NOT NULL,
    -- Stores BCrypt hash, NOT plain text. BCrypt hashes look like: $2a$12$xxxx...

                       full_name     VARCHAR(255),
    -- Nullable — user doesn't have to provide full name

                       api_key       VARCHAR(64) UNIQUE,
    -- Optional API key for programmatic access (future feature)

                       plan          VARCHAR(20)  NOT NULL DEFAULT 'FREE',
    -- FREE | PRO | TEAM — which subscription tier the user is on

                       reviews_used  INT          NOT NULL DEFAULT 0,
    -- How many reviews this user has used this month

                       reviews_limit INT          NOT NULL DEFAULT 5,
    -- How many reviews per month this user is allowed (5 on FREE tier)

                       created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    -- TIMESTAMPTZ = timestamp WITH timezone — always store timezone-aware timestamps

                       updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Indexes speed up queries that filter/sort by these columns
CREATE INDEX idx_users_email   ON users(email);
CREATE INDEX idx_users_api_key ON users(api_key);
-- Without these indexes, finding user by email does a full table scan (slow at scale)
