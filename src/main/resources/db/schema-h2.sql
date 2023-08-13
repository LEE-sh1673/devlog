drop table IF exists post cascade;
drop table IF exists user cascade;
drop table IF exists event cascade;
drop table IF exists event_tag cascade;
drop table IF exists tag cascade;
drop table IF exists comment cascade;

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

alter table post add CONSTRAINT FK_POST_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

create TABLE comment (
    id bigint not null auto_increment,
    author VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    content VARCHAR(255) NOT NULL,
    post_id bigint NULL,
    CONSTRAINT pk_comment PRIMARY KEY (id)
);

alter table comment ADD CONSTRAINT FK_COMMENT_ON_POST FOREIGN KEY (post_id) REFERENCES post (id);

create index IDX_COMMENT_POST_ID on comment (post_id);


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