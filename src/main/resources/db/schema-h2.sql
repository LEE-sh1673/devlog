DROP TABLE IF EXISTS post CASCADE;

CREATE TABLE Post
(
  id bigint NOT NULL AUTO_INCREMENT,
  title varchar(255),
  content clob,
  PRIMARY KEY (id)
);
