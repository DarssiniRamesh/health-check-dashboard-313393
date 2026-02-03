-- V1__baseline.sql
-- Initial baseline migration for backend_api
-- 
-- This migration represents the current Neon Postgres schema as documented in
-- kavia-docs/derived-schema.sql.md (derived from the legacy monolith).
--
-- Contains: Tables, indexes, constraints, sequences (via IDENTITY columns)
-- Excludes: Sample data (handled by SampleDataInitializer at runtime)
--
-- Rollback Strategy:
-- To rollback this migration, manually DROP all tables in reverse dependency order:
--   DROP TABLE public.billable_hours;
--   DROP TABLE public.billing_categories;
--   DROP TABLE public.customers;
--   DROP TABLE public.users;
--
-- IMPORTANT: This baseline is intended for use with FLYWAY_BASELINE_ON_MIGRATE=true
-- when applying Flyway to an existing database that already has this schema.

-- =============================================================================
-- Tables
-- =============================================================================

-- users table
-- Stores user information with email uniqueness constraint
CREATE TABLE IF NOT EXISTS public.users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT users_email_unique UNIQUE (email)
);

-- customers table
-- Stores customer/client information
CREATE TABLE IF NOT EXISTS public.customers (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    address VARCHAR(500),
    created_at TIMESTAMP NOT NULL
);

-- billing_categories table
-- Stores billing category definitions with hourly rates
CREATE TABLE IF NOT EXISTS public.billing_categories (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    hourly_rate NUMERIC(10,2) NOT NULL
);

-- billable_hours table
-- Stores logged billable hours with foreign key relationships
CREATE TABLE IF NOT EXISTS public.billable_hours (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    hours NUMERIC(8,2) NOT NULL,
    note VARCHAR(1000),
    date_logged DATE NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT billable_hours_customer_fk FOREIGN KEY (customer_id) REFERENCES public.customers(id),
    CONSTRAINT billable_hours_user_fk FOREIGN KEY (user_id) REFERENCES public.users(id),
    CONSTRAINT billable_hours_category_fk FOREIGN KEY (category_id) REFERENCES public.billing_categories(id)
);

-- =============================================================================
-- Indexes
-- =============================================================================
-- Foreign key indexes for performance optimization on billable_hours table

CREATE INDEX IF NOT EXISTS idx_billable_hours_customer_id ON public.billable_hours (customer_id);
CREATE INDEX IF NOT EXISTS idx_billable_hours_user_id ON public.billable_hours (user_id);
CREATE INDEX IF NOT EXISTS idx_billable_hours_category_id ON public.billable_hours (category_id);

-- =============================================================================
-- Notes
-- =============================================================================
-- 
-- 1. Sequences: PostgreSQL IDENTITY columns automatically create sequences
--    (users_id_seq, customers_id_seq, billing_categories_id_seq, billable_hours_id_seq)
--
-- 2. Sample Data: NOT included in this migration. The application uses
--    SampleDataInitializer.java to seed data at runtime when tables are empty.
--
-- 3. SSL Requirement: Neon requires sslmode=require. Connection string handling
--    is managed by DatabaseUrlEnvironmentPostProcessor.java
--
-- 4. Schema Version: This represents version 1.0.0 as documented. Future schema
--    changes should be added as new versioned migrations (V1.0.1, V1.1.0, etc.)
