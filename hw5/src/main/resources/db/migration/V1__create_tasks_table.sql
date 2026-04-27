CREATE TABLE IF NOT EXISTS tasks
(
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    priority    VARCHAR(20)  NOT NULL,
    completed   BOOLEAN      NOT NULL DEFAULT FALSE,
    due_date    DATE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS task_tags
(
    task_id BIGINT      NOT NULL REFERENCES tasks (id) ON DELETE CASCADE,
    tag     VARCHAR(50) NOT NULL,
    PRIMARY KEY (task_id, tag)
);

CREATE TABLE IF NOT EXISTS task_attachments
(
    id          BIGSERIAL PRIMARY KEY,
    task_id     BIGINT       NOT NULL REFERENCES tasks (id) ON DELETE CASCADE,
    filename    VARCHAR(255) NOT NULL,
    file_path   VARCHAR(500) NOT NULL,
    uploaded_at TIMESTAMP    NOT NULL DEFAULT NOW()
);
