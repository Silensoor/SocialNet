delete from friendships;
delete from block_history;
delete from likes;
delete from notifications;
delete from messages;
delete from dialogs;
delete from post_comments;
delete from post2tag;
delete from posts;

delete from persons;

insert into persons (id, about, birth_date, change_password_token, configuration_code, deleted_time, email, first_name, is_approved, is_blocked, is_deleted, last_name, last_online_time, message_permissions, notifications_session_id, online_status, password, phone, photo, reg_date, city, country, telegram_id, person_settings_id) values ('1', 'A rookie, who started his day with a wrong foot.', '1972-11-14 21:25:19', 'xfolip091', '1', '2022-04-15 00:43:45', 'user1@email.com', 'Leon', true, false, false, 'Kennedy', '2022-07-21 14:45:29', 'adipiscing', 'ipsum', 'accumsan', '$2a$10$DKfACXByOkjee4VELDw7R.BeslHcGeeLbCK2N8gV3.BaYjSClnObG', '966-998-0544', 'go86atavdxhcvcagbv', '2000-07-26 16:21:43', 'Bourg-en-Bresse', 'France', 93, 633);
insert into persons (id, about, birth_date, change_password_token, configuration_code, deleted_time, email, first_name, is_approved, is_blocked, is_deleted, last_name, last_online_time, message_permissions, notifications_session_id, online_status, password, phone, photo, reg_date, city, country, telegram_id, person_settings_id) values ('2', 'Racoon city survivor.', '1972-03-16 21:08:10', 'egfsab435', '5986', '2022-12-31 15:50:27', 'user2@email.com', 'Jill', true, false, false, 'Valentine', '2022-06-05 08:43:54', 'condimentum', 'gravida', 'posuere', '$2a$10$DKfACXByOkjee4VELDw7R.BeslHcGeeLbCK2N8gV3.BaYjSClnObG', '723-747-2350', 'iq04yazuvhqohqplwb', '2007-01-13 15:46:25', 'Carman', 'Canada', 630, 329);
insert into persons (id, about, birth_date, change_password_token, configuration_code, deleted_time, email, first_name, is_approved, is_blocked, is_deleted, last_name, last_online_time, message_permissions, notifications_session_id, online_status, password, phone, photo, reg_date, city, country, telegram_id, person_settings_id) values ('3', 'Ut at dolor quis odio consequat varius.', '2009-08-13 09:02:18', 'otvmuy632', '39653', '2022-06-16 06:30:33', 'kutting1@eventbrite.com', 'Kermie', false, false, true, 'Utting', '2022-07-20 14:36:53', 'eu', 'ipsum', 'vivamus', '$2a$10$DKfACXByOkjee4VELDw7R.BeslHcGeeLbCK2N8gV3.BaYjSClnObG', '250-782-9421', 'zt05sdtbjpljybzabu', '2000-05-06 10:21:31', 'Al Qadarif', 'Sudan', 714, 308);
