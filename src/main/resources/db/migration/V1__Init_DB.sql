create sequence hibernate_sequence start 2 increment 1;

create table message (
    id int8 not null,
    date timestamp,
    filename varchar(255),
    tag varchar(255),
    text varchar(2048),
    usr_id int8, primary key (id)
);

create table usr (
    id int8 not null,
    activation_code varchar(255),
    active boolean not null,
    email varchar(255),
    password varchar(255) not null,
    username varchar(255) not null,
    primary key (id)
);

create table usr_role (
    usr_id int8 not null,
    roles varchar(255)
);

alter table if exists message
    add constraint message_user_fk
    foreign key (usr_id) references usr;

alter table if exists usr_role
    add constraint user_role_user_fk
    foreign key (usr_id) references usr;