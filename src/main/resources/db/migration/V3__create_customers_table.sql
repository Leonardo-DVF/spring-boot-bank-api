CREATE TABLE customers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    full_name VARCHAR(120) NOT NULL,
    document VARCHAR(11) NOT NULL UNIQUE,
    status VARCHAR(15) NOT NULL,

    user_id UUID NOT NULL UNIQUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_customers_user_id ON customers(user_id);