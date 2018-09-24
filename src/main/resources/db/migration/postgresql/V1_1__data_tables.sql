CREATE TABLE IF NOT EXISTS customers (
   customer_id    CHAR(256) PRIMARY KEY      NOT NULL,
   data           CHAR(25000) NOT NULL
);


CREATE TABLE IF NOT EXISTS profileimages (
   customer_id    CHAR(256) PRIMARY KEY      NOT NULL,
   image BYTEA,
   contenttype    CHAR(256)
);

GRANT ALL ON customers TO crmadmin;
GRANT ALL ON profileimages TO crmadmin;
