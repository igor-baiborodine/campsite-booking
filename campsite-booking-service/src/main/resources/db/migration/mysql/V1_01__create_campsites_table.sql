CREATE TABLE campsites
(
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    capacity       INT          NOT NULL,
    restrooms      BOOLEAN      NOT NULL,
    drinking_water BOOLEAN      NOT NULL,
    picnic_table   BOOLEAN      NOT NULL,
    fire_pit       BOOLEAN      NOT NULL,
    active         BOOLEAN      NOT NULL,
    created_at     TIMESTAMP(6) NOT NULL,
    updated_at     TIMESTAMP(6) NOT NULL,
    CONSTRAINT pk_campsites PRIMARY KEY (id)
);
