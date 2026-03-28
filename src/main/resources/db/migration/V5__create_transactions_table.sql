CREATE TABLE transactions (
    id UUID PRIMARY KEY,

    account_id UUID NOT NULL,

    type VARCHAR(20) NOT NULL
        CHECK ( type IN ('DEPOSIT', 'WITHDRAW', 'TRANSFER')),

    amount NUMERIC(19, 2) NOT NULL,

    balance_before NUMERIC(19, 2) NOT NULL,
    balance_after NUMERIC(19, 2) NOT NULL,

    to_account_id UUID,

    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_transactions_account_id ON transactions (account_id);
CREATE INDEX idx_transactions_created_at ON transactions (created_at);