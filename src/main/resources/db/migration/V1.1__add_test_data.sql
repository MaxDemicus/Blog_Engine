insert into users (code, email, is_moderator, name, password, photo, reg_time)
values ('code1', 'email1@mail.ru', 1, 'name1', '$2a$12$w3FAazMc7Pe0iPbdZKHrfuo13mq1VfN6Hbb3eF4K5V0wkyKUaQRpG', 'photo1', '2019-08-01 07:07:07'),
(NULL, 'email2@mail.ru', 1, 'name2', '$2a$12$shsWMGAcNIwQGXOWDcU3meZ3RrUT4op2AD0SpVyWQxS3Klsaj/hYi', 'photo2', '2010-04-10 08:15:00'),
('code3', 'email3@mail.ru', 0, 'name3', '$2a$12$wXznalCWRFQaRJvaNFKQy.Cm1D.aR0sFR2mpGQRT9wjz2JTGiXnbS', NULL, '2021-01-01 00:00:00');

insert into posts (is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
values (1, 'NEW',  'text1', '2013-12-07 12:42:06', 'title1', 2, NULL, 3),
(1, 'ACCEPTED',  'text2', '2008-10-25 05:32:26', 'title2', 3, 1, 3),
(0, 'DECLINED',  'text3', '2015-11-20 15:01:59', 'title3', 0, 1, 1),
(1, 'ACCEPTED',  'text4', '2018-01-01 02:02:02', 'title4', 3, 2, 3),
(1, 'ACCEPTED',  'text5', '2017-12-20 06:34:20', 'title5', 10, 1, 2),
(1, 'ACCEPTED',  'text6', '2009-01-25 07:36:16', 'title6', 7, 1, 3),
(1, 'ACCEPTED',  'text7', '2013-03-03 08:38:00', 'title7', 0, 2, 2),
(1, 'ACCEPTED',  'text8', '2020-05-28 09:40:59', 'title8', 3, 1, 3),
(1, 'ACCEPTED',  'text9', '2023-10-17 10:50:44', 'title9', 5, 1, 3),
(1, 'ACCEPTED',  'text10', '2022-01-07 12:42:06', 'title10', 2, 2, 1),
(1, 'ACCEPTED',  'text11', '2022-01-07 05:32:26', 'title11', 3, 1, 2),
(1, 'ACCEPTED',  'text12', '2022-02-01 09:12:48', 'title12', 7, 1, 3);

insert into post_comments (text, time, parent_id, post_id, user_id)
values ('comment1_to_post_1', '2020-11-01 08:09:48', NULL, 1, 1),
('comment2_to_post_1', '2020-04-09 14:53:03', 1, 1, 2),
('comment1_to_post2', '2012-10-29 10:12:37', NULL, 2, 3),
('comment1_to_post_4', '2020-04-09 14:37:03', NULL, 4, 2),
('comment2_to_post_4', '2021-01-08 12:24:56', 4, 4, 1),
('comment1_to_post_5', '2012-04-09 14:02:34', NULL, 5, 2),
('comment2_to_post_5', '2016-07-09 16:09:57', 6, 5, 1),
('comment3_to_post_5', '2020-01-09 10:58:12', 6, 5, 3);

insert into post_votes (time, value, post_id, user_id)
values ('2021-03-27 10:22:42', 1, 1, 1),
('2020-08-07 07:57:23', 1, 2, 1),
('2021-07-09 01:50:28', 1, 2, 2),
('2019-01-02 23:23:52', -1, 2, 3),
('2021-07-09 01:50:28', 1, 4, 1),
('2021-07-09 01:50:28', 1, 4, 2),
('2021-07-09 01:50:28', 1, 4, 3),
('2021-07-09 01:50:28', -1, 5, 1),
('2021-07-09 01:50:28', -1, 5, 2),
('2021-07-09 01:50:28', -1, 5, 3),
('2021-07-09 01:50:28', -1, 5, 3);

insert into tags (name)
values ('tag1 для постов 1 и 2'), ('tag2 для поста 1'), ('tag3 без постов'), ('tag4 для постов 4 и 5');

insert into tag2post (post_id, tag_id)
values (1, 1), (1, 2), (2, 1), (4, 4), (5, 4);