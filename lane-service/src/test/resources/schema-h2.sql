DROP TABLE IF EXISTS lanes;

CREATE TABLE lanes (
                       id VARCHAR(255) PRIMARY KEY,
                       lane_number INTEGER NOT NULL,
                       zone VARCHAR(255),
                       status VARCHAR(50)
);
