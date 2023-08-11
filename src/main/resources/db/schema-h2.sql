drop table IF exists post cascade;
drop table IF exists user cascade;
drop table IF exists event cascade;
drop table IF exists event_tag cascade;
drop table IF exists tag cascade;

create table Post (
  id bigint NOT NULL AUTO_INCREMENT,
  title varchar(255),
  content clob,
  user_id bigint NULL,
  PRIMARY KEY (id),
  CONSTRAINT pk_post PRIMARY KEY (id)
);

create table user (
  id bigint not null auto_increment,
  name varchar(255),
  email varchar(255),
  password varchar(255),
  created_at datetime(6),
  primary key (id)
);

ALTER TABLE post ADD CONSTRAINT FK_POST_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);


create table event (
    id bigint not null auto_increment,
    email varchar(255),
    title varchar(255),
    primary key (id)
);

create table event_tag (
    event_tag_id bigint not null auto_increment,
    event_id bigint not null,
    tag_id bigint not null,
    primary key (event_tag_id)
);

create table tag (
    id bigint not null auto_increment,
    name varchar(255),
    primary key (id)
);