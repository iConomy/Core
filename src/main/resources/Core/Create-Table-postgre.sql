CREATE TABLE "%1s" (
  "id" bigserial NOT NULL PRIMARY KEY,
  "username" character varying(32) NOT NULL,
  "balance" numeric(64,2) NOT NULL,
  "status" smallint DEFAULT 0 NOT NULL,
  CONSTRAINT "username" UNIQUE ("username")
);