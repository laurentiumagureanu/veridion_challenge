CREATE SEQUENCE scrape_batch_id_seq INCREMENT BY 1;

CREATE TABLE scrape_batch (
    id bigint NOT NULL,
    domains varchar[] NULL,
    created_at timestamp NOT NULL,
    updated_at timestamp NULL,
    version int8 NOT NULL DEFAULT 1,
    CONSTRAINT scrape_batch_pk PRIMARY KEY (id)
);

CREATE SEQUENCE scrape_job_id_seq INCREMENT BY 1;

CREATE TABLE scrape_job (
    id bigint NOT NULL,
    "domain" varchar NOT NULL,
    status varchar NOT NULL,
    created_at timestamp NOT NULL,
    updated_at varchar NULL,
    batch_id bigint NULL,
    version int8 NOT NULL DEFAULT 1,
    CONSTRAINT scrape_job_pk PRIMARY KEY (id),
    CONSTRAINT scrape_job_scrape_batch_fk FOREIGN KEY (batch_id) REFERENCES scrape_batch(id)
);

CREATE SEQUENCE scrape_job_event_id_seq INCREMENT BY 1;

CREATE TABLE scrape_job_event (
    id bigint NOT NULL,
    scrape_job_id bigint NOT NULL,
    event_type varchar NOT NULL,
    event_details varchar NULL,
    created_at timestamp NOT NULL,
    version int8 NOT NULL DEFAULT 1,
    CONSTRAINT scrape_job_event_pk PRIMARY KEY (id),
    CONSTRAINT scrape_job_event_scrape_job_fk FOREIGN KEY (scrape_job_id) REFERENCES scrape_job(id)
);

CREATE SEQUENCE company_id_seq INCREMENT BY 1;

CREATE TABLE company (
    id int8 NOT NULL,
    "domain" varchar NOT NULL,
    website varchar NOT NULL,
    commercial_name varchar NULL,
    legal_name varchar NULL,
    all_names varchar[] NULL,
    address varchar NULL,
    phone_numbers varchar[] NULL,
    social_media_links varchar[] NULL,
    created_at timestamp NOT NULL,
    updated_at timestamp NULL,
    version int8 NOT NULL DEFAULT 1,
    CONSTRAINT company_pk PRIMARY KEY (id),
    CONSTRAINT company_unique UNIQUE (domain)
);