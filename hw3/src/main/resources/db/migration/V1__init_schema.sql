CREATE TABLE tasks (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(100)  NOT NULL,
    description VARCHAR(500),
    completed   BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP,
    last_modified_at TIMESTAMP,
    due_date    DATE,
    priority    VARCHAR(20)
);

CREATE TABLE task_tags (
    task_id BIGINT      NOT NULL REFERENCES tasks (id) ON DELETE CASCADE,
    tag     VARCHAR(100) NOT NULL
);

CREATE TABLE task_attachments (
    id                BIGSERIAL PRIMARY KEY,
    task_id           BIGINT       NOT NULL REFERENCES tasks (id) ON DELETE CASCADE,
    file_name         VARCHAR(255) NOT NULL,
    stored_file_name  VARCHAR(255) NOT NULL,
    content_type      VARCHAR(100),
    size              BIGINT       NOT NULL,
    uploaded_at       TIMESTAMP
);
