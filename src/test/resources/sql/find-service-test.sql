insert into Persons (email, first_name, last_name, birth_date, is_approved, is_blocked, is_deleted, city) values
('test_user1@email.com', 'Firstname1', 'Lastname1', now() - interval '1 year', true,false,false, 'Moscow'),
('test_user2@email.com', 'Firstname2', 'Lastname2', now() - interval '2 year', true,false,false, 'Novokuznetsk'),
('test_user3@email.com', 'Firstname3', 'Lastname3', now() - interval '3 year', true,false,false, 'Sochi')