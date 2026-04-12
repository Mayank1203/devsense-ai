-- V2__create_reviews.sql

CREATE TABLE reviews (
                         id             UUID         PRIMARY KEY DEFAULT gen_random_uuid(),

                         user_id        UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    -- REFERENCES users(id) = foreign key — must exist in users table
    -- ON DELETE CASCADE = if user is deleted, their reviews are deleted too

                         repo_url       VARCHAR(500) NOT NULL,
    -- The GitHub URL the user submitted: https://github.com/owner/repo

                         repo_owner     VARCHAR(255),
    -- Extracted from repo_url: 'owner' part

                         repo_name      VARCHAR(255),
    -- Extracted from repo_url: 'repo' part

                         language       VARCHAR(50),
    -- Programming language: java, python, javascript etc.

                         focus_areas    TEXT[],
    -- PostgreSQL array — stores multiple strings: {security,performance,style}

                         status         VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    -- PENDING → PROCESSING → COMPLETED or FAILED
    -- Client polls this until COMPLETED

                         overall_score  DECIMAL(4,2),
    -- Score from 0.00 to 10.00. Null until review is COMPLETED.

                         summary        TEXT,
    -- AI-generated 2-3 sentence summary of the review

                         error_message  TEXT,
    -- Filled in when status = FAILED. Shown to user.

                         total_files    INT          NOT NULL DEFAULT 0,
                         files_reviewed INT          NOT NULL DEFAULT 0,
    -- Progress counters for showing 'reviewing 5 of 12 files...'

                         started_at     TIMESTAMPTZ,
                         completed_at   TIMESTAMPTZ,
                         created_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_reviews_user_id  ON reviews(user_id);
CREATE INDEX idx_reviews_status   ON reviews(status);
CREATE INDEX idx_reviews_created  ON reviews(created_at DESC);
-- DESC index = newest reviews first (most common query pattern)
