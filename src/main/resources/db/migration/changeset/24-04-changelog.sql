-- liquibase formatted sql

-- changeset HP:1758733211714-1
CREATE EXTENSION IF NOT EXISTS pgcrypto;
INSERT INTO accounts VALUES (gen_random_uuid(), 'admin', 'admin@admin.ru', 'Admin', 'Admin','$2a$10$MTAHT6PodKKfXuw8a2rAru4nuYj6rA9mloElGb/L..y1mkNKU/7NW', now(),0,0 );

-- changeset HP:1758733211714-3
CREATE UNIQUE INDEX card_unique_owner_status_encrypted_number
ON cards (owner, card_number_hash);

