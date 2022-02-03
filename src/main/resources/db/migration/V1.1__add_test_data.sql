insert into users (code, email, is_moderator, name, password, photo, reg_time)
values ("code1", "email1", 1, "name1", "password1", "photo1", '2019-08-01 07:07:07'),
(NULL, "email2", 1, "name2", "password2", "photo2", '2010-04-10 08:15:00'),
("code3", "email3", 0, "name3", "password3", NULL, '2021-01-01 00:00:00');

insert into posts (is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
values (1, "NEW",  "text1", '2013-12-07 12:42:06', "title1", 2, NULL, 3),
(1, "ACCEPTED",  "text2", '2008-10-25 05:32:26', "title2", 3, 1, 3),
(0, "DECLINED",  "text3", '2015-11-20 15:01:59', "title3", 0, 1, 1);

insert into post_comments (text, time, parent_id, post_id, user_id)
values ("comment1_to_post_1", '2020-11-01 08:09:48', NULL, 1, 1),
("comment2_to_post_1", '2020-04-09 14:53:03', 1, 1, 2),
("comment1_to_post2", '2012-10-29 10:12:37', NULL, 2, 3);

insert into post_votes (time, value, post_id, user_id)
values ('2021-03-27 10:22:42', 1, 1, 1),
('2020-08-07 07:57:23', 1, 2, 1),
('2019-01-02 23:23:52', -1, 2, 3);

insert into tags (name)
values ("tag1"), ("tag2"), ("tag3");

insert into tag2post (post_id, tag_id)
values (1, 1), (1, 2), (2, 3);