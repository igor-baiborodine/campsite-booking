CREATE TABLE campsites
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    created_at     datetime NOT NULL,
    updated_at     datetime NULL,
    capacity       INT      NOT NULL,
    restrooms      BIT(1)   NOT NULL,
    drinking_water BIT(1)   NOT NULL,
    picnic_table   BIT(1)   NOT NULL,
    fire_pit       BIT(1)   NOT NULL,
    active         BIT(1)   NOT NULL,
    CONSTRAINT pk_campsites PRIMARY KEY (id)
);
