ALTER TABLE users
    ALTER COLUMN id DROP DEFAULT;

ALTER TABLE customers
    ADD CONSTRAINT fk_customers_user
        FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE accounts
    ADD CONSTRAINT fk_accounts_customer
        FOREIGN KEY (customer_id) REFERENCES customers(id);

ALTER TABLE transactions
    ADD CONSTRAINT fk_transactions_account
        FOREIGN KEY (account_id) REFERENCES accounts(id);

ALTER TABLE transactions
    ADD CONSTRAINT fk_transactions_to_account
        FOREIGN KEY (to_account_id) REFERENCES accounts(id);