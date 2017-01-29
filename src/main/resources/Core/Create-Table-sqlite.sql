CREATE TABLE %1s (id INTEGER PRIMARY KEY,username varchar(32) NOT NULL,balance double(64,2) NOT NULL,status int(2) NOT NULL DEFAULT 0);
CREATE UNIQUE INDEX username ON iconomy (username);