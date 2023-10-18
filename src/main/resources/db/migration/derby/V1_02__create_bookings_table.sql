CREATE TABLE bookings
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    uuid        VARCHAR(255) NOT NULL,
    version     BIGINT       NOT NULL,
    campsite_id BIGINT       NOT NULL,
    email       VARCHAR(50)  NOT NULL,
    full_name   VARCHAR(50)  NOT NULL,
    start_date  DATE         NOT NULL,
    end_date    DATE         NOT NULL,
    active      BOOLEAN      NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL,
    CONSTRAINT pk_bookings PRIMARY KEY (id)
);

ALTER TABLE bookings
    ADD CONSTRAINT uc_bookings_uuid UNIQUE (uuid);

ALTER TABLE bookings
    ADD CONSTRAINT fk_campsites_id
        FOREIGN KEY (campsite_id) REFERENCES campsites(id)
            ON DELETE RESTRICT ON UPDATE RESTRICT;
