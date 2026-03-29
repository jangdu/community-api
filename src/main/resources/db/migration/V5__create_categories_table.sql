CREATE TABLE categories (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    name          VARCHAR(30)  NOT NULL,
    slug          VARCHAR(30)  NOT NULL,
    display_order INT          NOT NULL DEFAULT 0,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_categories_slug (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO categories (name, slug, display_order) VALUES
    ('자유게시판', 'free', 1),
    ('질문', 'question', 2),
    ('정보공유', 'info', 3);
