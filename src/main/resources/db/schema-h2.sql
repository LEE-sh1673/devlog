drop table IF exists post cascade;
drop table IF exists user cascade;

create table Post
(
  id bigint NOT NULL AUTO_INCREMENT,
  title varchar(255),
  content clob,
  PRIMARY KEY (id)
);

create table user (
  id bigint not null auto_increment,
  name varchar(255),
  email varchar(255),
  password varchar(255),
  created_at datetime(6),
  primary key (id)
);