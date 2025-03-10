--liquibase formatted sql
--changeset junior:2025030040
--comment: blocks table create

CREATE TABLE blocks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    blocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    block_reason VARCHAR(255) NOT NULL,
    unblocked_at TIMESTAMP NULL,
    unblock_reason VARCHAR(255) NOT NULL,
    card_id BIGINT NOT NULL,
    CONSTRAINT cards_blocks_fk FOREIGN KEY (card_id) REFERENCES cards(id) ON DELETE CASCADE
) ENGINE=InnoDB;
--rollback DROP TABLE BLOCKS


