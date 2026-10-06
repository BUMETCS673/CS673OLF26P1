-- AI-USAGE SUMMARY
-- Tools: Gemini
-- Overall AI Contribution: 100%
-- AI-Assisted Areas: Consolidated DDL ALTER script combining column addition and comment modification.
-- Human Contributions: Database migration execution and verification.
-- Notes: Initial code generated via AI; requires student verification, refactoring, and testing before integration.
-- Authors: Sara Orion

ALTER TABLE inventory_movements
    ADD COLUMN user_id VARCHAR(36) NULL AFTER reference_id,
    ADD CONSTRAINT fk_movement_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    MODIFY COLUMN reference_id VARCHAR(36) NULL
        COMMENT 'Polymorphic reference ID based on movement_types.code: SALE -> sale_transactions.id; RESTOCK -> Supplier Purchase Order (NULL if manual intake); ADJUSTMENT -> Stock Audit ID (NULL if manual adjustment); RETURN -> Customer Return Receipt / Original Sale ID';