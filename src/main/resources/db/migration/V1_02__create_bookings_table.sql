CREATE TABLE bookings
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    created_at  datetime     NOT NULL,
    updated_at  datetime     NULL,
    uuid        VARCHAR(255) NOT NULL,
    version     BIGINT       NOT NULL,
    campsite_id BIGINT       NOT NULL,
    email       VARCHAR(50)  NOT NULL,
    full_name   VARCHAR(50)  NOT NULL,
    start_date  date         NOT NULL,
    end_date    date         NOT NULL,
    active      BIT(1)       NOT NULL,
    CONSTRAINT pk_bookings PRIMARY KEY (id)
);

ALTER TABLE bookings
    ADD CONSTRAINT uc_bookings_uuid UNIQUE (uuid);

ALTER TABLE bookings
    ADD CONSTRAINT fk_campsites
        FOREIGN KEY (campsite_id) REFERENCES campsites(id)
            ON DELETE RESTRICT ON UPDATE RESTRICT;
