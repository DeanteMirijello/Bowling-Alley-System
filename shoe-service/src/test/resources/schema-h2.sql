DROP TABLE IF EXISTS shoes;

CREATE TABLE shoes (
                       id VARCHAR(255) PRIMARY KEY,
                       size VARCHAR(50),
                       purchase_date DATE,
                       status VARCHAR(50)
);
