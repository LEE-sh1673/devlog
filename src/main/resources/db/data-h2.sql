INSERT INTO post (title, content) VALUES ('글 제목 1', '글 본문 1');
INSERT INTO post (title, content) VALUES ('글 제목 2', '글 본문 2');
INSERT INTO post (title, content) VALUES ('글 제목 3', '글 본문 3');
INSERT INTO post (title, content) VALUES ('글 제목 4', '글 본문 4');
INSERT INTO post (title, content) VALUES ('글 제목 5', '글 본문 5');
INSERT INTO post (title, content) VALUES ('글 제목 6', '글 본문 6');
INSERT INTO post (title, content) VALUES ('글 제목 7', '글 본문 7');
INSERT INTO post (title, content) VALUES ('글 제목 8', '글 본문 8');
INSERT INTO post (title, content) VALUES ('글 제목 9', '글 본문 9');
INSERT INTO post (title, content) VALUES ('글 제목 10', '글 본문 10');

INSERT INTO user (name, email, password, created_at)
VALUES('이승훈', 'lsh901673@gmail.com', '1234', '2023-07-18 17:41:00');

INSERT INTO event (email, title) VALUES('lsh901673@gmail.com', 'title 1');
INSERT INTO event (email, title) VALUES('lsh901673@gmail.com', 'title 2');
INSERT INTO event (email, title) VALUES('lsh901673@gmail.com', 'title 3');
INSERT INTO event (email, title) VALUES('lsh901673@gmail.com', 'title 4');
INSERT INTO event (email, title) VALUES('lsh901673@gmail.com', 'title 5');
INSERT INTO event (email, title) VALUES('lsh901673@gmail.com', 'title 6');
INSERT INTO event (email, title) VALUES('lsh901673@gmail.com', 'title 7');

INSERT INTO tag(name) VALUES('food');
INSERT INTO tag(name) VALUES('delivery');
INSERT INTO tag(name) VALUES('coding');

INSERT INTO event_tag (event_id, tag_id) VALUES(1, 1);
INSERT INTO event_tag (event_id, tag_id) VALUES(1, 2);
INSERT INTO event_tag (event_id, tag_id) VALUES(2, 1);
INSERT INTO event_tag (event_id, tag_id) VALUES(2, 3);
INSERT INTO event_tag (event_id, tag_id) VALUES(3, 2);
