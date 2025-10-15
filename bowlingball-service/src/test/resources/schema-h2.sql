DROP TABLE IF EXISTS bowling_balls;

CREATE TABLE bowling_balls (
                               id VARCHAR(255) PRIMARY KEY,
                               size VARCHAR(50),
                               grip_type VARCHAR(100),
                               color VARCHAR(100),
                               status VARCHAR(50)
);
