alter table post_comments add foreign key (parent_id) references post_comments (id);
alter table post_comments add foreign key (post_id) references posts (id);
alter table post_comments add foreign key (user_id) references users (id);
alter table post_votes add foreign key (post_id) references posts (id);
alter table post_votes add foreign key (user_id) references users (id);
alter table posts add foreign key (moderator_id) references users (id);
alter table posts add foreign key (user_id) references users (id);
alter table tag2post add foreign key (tag_id) references tags (id);
alter table tag2post add foreign key (post_id) references posts (id);

