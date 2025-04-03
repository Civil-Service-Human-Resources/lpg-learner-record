CREATE TABLE learner_record_types
(
    id          INT PRIMARY KEY,
    record_type VARCHAR(50) NOT NULL,
    CONSTRAINT uk_learner_record_types_type UNIQUE (record_type)
);

CREATE TABLE learner_records
(
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    learner_record_type INT         NOT NULL,
    learner_record_uid  VARCHAR(50) NOT NULL,
    learner_id          VARCHAR(50) NOT NULL,
    resource_id         VARCHAR(50) NOT NULL,
    created_timestamp   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    parent_record_id    BIGINT      NULL,
    is_archived         BOOLEAN     NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_learner_records_type
        FOREIGN KEY (learner_record_type) REFERENCES learner_record_types (id),
    CONSTRAINT fk_learner_records_parent
        FOREIGN KEY (parent_record_id) REFERENCES learner_records (id) ON DELETE SET NULL,
    CONSTRAINT uk_learner_record
        UNIQUE (learner_record_type, learner_id, resource_id)
);

CREATE INDEX idx_learner_records_type ON learner_records (learner_record_type);
CREATE INDEX idx_learner_records_learner ON learner_records (learner_id);
CREATE INDEX idx_learner_records_resource ON learner_records (resource_id);
CREATE INDEX idx_learner_records_parent ON learner_records (parent_record_id);

CREATE TABLE learner_record_event_types
(
    id          INT PRIMARY KEY,
    record_type INT          NOT NULL,
    event_type  VARCHAR(50)  NOT NULL,
    description VARCHAR(255) NOT NULL,
    CONSTRAINT uk_learner_event_type
        UNIQUE (record_type, event_type),
    CONSTRAINT fk_learner_event_types_record_type
        FOREIGN KEY (record_type) REFERENCES learner_record_types (id)
);

CREATE INDEX idx_learner_event_types_record_type ON learner_record_event_types (record_type);

CREATE TABLE learner_record_event_sources
(
    id          INT PRIMARY KEY,
    source      VARCHAR(50)  NOT NULL,
    description VARCHAR(255) NOT NULL,
    CONSTRAINT uk_learner_event_sources_source UNIQUE (source)
);

CREATE TABLE learner_record_events
(
    id                          BIGINT PRIMARY KEY AUTO_INCREMENT,
    learner_record_id           BIGINT    NOT NULL,
    learner_record_event_type   INT       NOT NULL,
    learner_record_event_source INT       NOT NULL,
    event_timestamp             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_learner_events_record
        FOREIGN KEY (learner_record_id) REFERENCES learner_records (id),
    CONSTRAINT fk_learner_events_type
        FOREIGN KEY (learner_record_event_type) REFERENCES learner_record_event_types (id),
    CONSTRAINT fk_learner_events_source
        FOREIGN KEY (learner_record_event_source) REFERENCES learner_record_event_sources (id)
);

CREATE INDEX idx_learner_events_record ON learner_record_events (learner_record_id);
CREATE INDEX idx_learner_events_type ON learner_record_events (learner_record_event_type);
CREATE INDEX idx_learner_events_source ON learner_record_events (learner_record_event_source);
CREATE INDEX idx_learner_events_timestamp ON learner_record_events (event_timestamp);
