insert into usr (id, username, password, active) values (1, 'admin', '12345', true);
insert into usr_role (usr_id, roles) values (1, 'USER'), (1, 'ADMIN');