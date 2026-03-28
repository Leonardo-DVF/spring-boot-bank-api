CREATE TABLE accounts (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                          customer_id UUID NOT NULL,

                          agency VARCHAR(10) NOT NULL,
                          number VARCHAR(20) NOT NULL,

                          type VARCHAR(30) NOT NULL,
                          status VARCHAR(20) NOT NULL,

                          balance NUMERIC(19, 2) NOT NULL DEFAULT 0.00,

                          created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                          updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                          CONSTRAINT uk_accounts_agency_number UNIQUE (agency, number)
);

CREATE INDEX idx_accounts_customer_id ON accounts(customer_id);
