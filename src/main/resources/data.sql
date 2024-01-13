BEGIN TRANSACTION;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS public.topic (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    name VARCHAR(255) NOT NULL ,
    created_at TIMESTAMPTZ DEFAULT current_timestamp,
    updated_at TIMESTAMPTZ DEFAULT current_timestamp,
    CONSTRAINT uk_topic_name UNIQUE(name)
    );

CREATE TABLE IF NOT EXISTS public.subscriber (
    subscriber_id VARCHAR(255) PRIMARY KEY,
    webhook_endpoint VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT current_timestamp,
    updated_at TIMESTAMPTZ DEFAULT current_timestamp
    );

CREATE TABLE IF NOT EXISTS public.topic_subscriber (
    topic_id UUID REFERENCES public.topic(id),
    subscriber_id VARCHAR(255) REFERENCES public.subscriber(subscriber_id),
    PRIMARY KEY (topic_id, subscriber_id)
    );


CREATE TABLE IF NOT EXISTS public.message (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    content TEXT NOT NULL,
    topic_id UUID REFERENCES public.topic(id),
    created_at TIMESTAMPTZ DEFAULT current_timestamp,
    updated_at TIMESTAMPTZ DEFAULT current_timestamp
    );

CREATE TABLE IF NOT EXISTS public.message_outbox (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    content TEXT NOT NULL,
    topic_id UUID REFERENCES public.topic(id),
    message_id UUID REFERENCES public.message(id),
    created_at TIMESTAMPTZ DEFAULT current_timestamp,
    updated_at TIMESTAMPTZ DEFAULT current_timestamp,
    status varchar(20) DEFAULT 'PENDING' not null
    );
CREATE INDEX  IF NOT EXISTS idx_status on public.message_outbox(status);
COMMIT;