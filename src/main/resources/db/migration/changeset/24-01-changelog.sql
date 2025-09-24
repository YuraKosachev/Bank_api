-- liquibase formatted sql

-- changeset HP:1758732690592-1
CREATE TABLE accounts (id UUID NOT NULL, username VARCHAR(255) NOT NULL, mail VARCHAR(255) NOT NULL, first_name VARCHAR(255) NOT NULL, last_name VARCHAR(255) NOT NULL, hashed_password VARCHAR(60) NOT NULL, created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, status SMALLINT NOT NULL, role SMALLINT NOT NULL, CONSTRAINT pk_accounts PRIMARY KEY (id));

-- changeset HP:1758732690592-2
CREATE TABLE cards (id UUID NOT NULL, owner VARCHAR(255) NOT NULL, account_id UUID NOT NULL, status SMALLINT NOT NULL, card_number_encrypted BYTEA NOT NULL, card_number_hash VARCHAR(64) NOT NULL, expired_in date NOT NULL, created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, blocked_request_at TIMESTAMP WITHOUT TIME ZONE, balance DECIMAL NOT NULL, CONSTRAINT pk_cards PRIMARY KEY (id));

-- changeset HP:1758732690592-3
CREATE TABLE tokens (id UUID NOT NULL, account_id UUID NOT NULL, access_token TEXT NOT NULL, refresh_token TEXT NOT NULL, access_token_expires TIMESTAMP WITHOUT TIME ZONE NOT NULL, refresh_token_expires TIMESTAMP WITHOUT TIME ZONE NOT NULL, status SMALLINT NOT NULL, created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, CONSTRAINT pk_tokens PRIMARY KEY (id));

-- changeset HP:1758732690592-4
ALTER TABLE accounts ADD CONSTRAINT uc_accounts_mail UNIQUE (mail);

-- changeset HP:1758732690592-5
ALTER TABLE accounts ADD CONSTRAINT uc_accounts_username UNIQUE (username);

-- changeset HP:1758732690592-6
ALTER TABLE cards ADD CONSTRAINT FK_CARDS_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES accounts (id);

-- changeset HP:1758732690592-7
ALTER TABLE tokens ADD CONSTRAINT FK_TOKENS_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES accounts (id);

