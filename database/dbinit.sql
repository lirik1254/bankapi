CREATE TABLE organisation_legal_forms (
                                       organisation_legal_form_id SERIAL PRIMARY KEY,
                                       name VARCHAR(255)
);

CREATE TABLE clients (
                         client_id SERIAL PRIMARY KEY,
                         name VARCHAR(255) not null,
                         shortname VARCHAR(255) not null,
                         organisation_legal_form_id INT check(organisation_legal_form_id >= 1) REFERENCES organisation_legal_forms(organisation_legal_form_id) ON DELETE SET NULL,
                         address VARCHAR(255) not null
);

CREATE TABLE banks (
    bank_id SERIAL PRIMARY KEY,
    bik VARCHAR(9) not null
);

CREATE TABLE deposits (
    deposit_id SERIAL PRIMARY KEY,
    client_id INT check(client_id >= 1) REFERENCES clients(client_id) ON DELETE SET NULL,
    bank_id INT check(bank_id >= 1) REFERENCES banks(bank_id) ON DELETE SET NULL,
    open_date DATE not null,
    percent DECIMAL not null check (percent >= 0),
    retention_period INT not null check (retention_period >= 0)
)