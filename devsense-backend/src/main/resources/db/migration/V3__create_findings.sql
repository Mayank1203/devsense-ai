-- V3__create_findings.sql

CREATE TABLE findings (
                          id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),

                          review_id     UUID         NOT NULL REFERENCES reviews(id) ON DELETE CASCADE,

                          agent_type    VARCHAR(20)  NOT NULL,
    -- Which agent found this: SECURITY | PERFORMANCE | STYLE

                          severity      VARCHAR(10)  NOT NULL,
    -- How serious: CRITICAL | HIGH | MEDIUM | LOW | INFO

                          category      VARCHAR(100),
    -- Specific issue type e.g. 'SQL Injection', 'N+1 Query', 'Missing Javadoc'

                          file_path     VARCHAR(500),
    -- Which file the issue is in: e.g. 'src/main/java/UserService.java'

                          line_start    INT,
                          line_end      INT,
    -- Line numbers of the problematic code

                          title         VARCHAR(500) NOT NULL,
    -- Short title: 'Potential SQL injection in UserService.findByEmail()'

                          description   TEXT         NOT NULL,
    -- Full explanation of the problem

                          suggestion    TEXT,
    -- How to fix it

                          code_snippet  TEXT,
    -- The actual problematic code (quoted from the file)

                          fixed_snippet TEXT,
    -- AI-suggested corrected version of the code

                          created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Embeddings table — used for RAG (Week 5)
-- Stores vector representations of code chunks for similarity search
CREATE TABLE code_embeddings (
                                 id          UUID     PRIMARY KEY DEFAULT gen_random_uuid(),
                                 review_id   UUID     REFERENCES reviews(id) ON DELETE CASCADE,
                                 file_path   VARCHAR(500),
                                 chunk_text  TEXT,
                                 embedding   vector(1536),
    -- vector(1536) is the pgvector type — stores a 1536-dimensional float array
    -- 1536 is the dimension of Anthropic's text embedding model
                                 created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_findings_review_id ON findings(review_id);
CREATE INDEX idx_findings_severity  ON findings(severity);

-- HNSW index for fast vector similarity search
-- Without this, pgvector does a full table scan on every similarity query (very slow)
CREATE INDEX idx_code_embeddings_vector
    ON code_embeddings USING hnsw (embedding vector_cosine_ops);
