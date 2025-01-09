CREATE TABLE refresh_token (
                               id SERIAL PRIMARY KEY,
                               token VARCHAR(255) NOT NULL,
                               expiry_date TIMESTAMP NOT NULL,
                               account_id BIGINT NOT NULL UNIQUE,
                               CONSTRAINT fk_refresh_token_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);