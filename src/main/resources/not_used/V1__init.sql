-- CREATE TABLE users
-- (
--     username VARCHAR(50)  NOT NULL,
--     password VARCHAR(100) NOT NULL,
--     real_name     VARCHAR(20)  NOT NULL,
--     enabled  boolean      NOT NULL,
--     PRIMARY KEY (username)
-- );


CREATE TABLE users
(
    username VARCHAR(50)  NOT NULL,
    password VARCHAR(150) NOT NULL,
    real_name     VARCHAR(20)  NOT NULL,
    enabled  boolean      NOT NULL,
    PRIMARY KEY (username)

);

INSERT INTO _user
VALUES ('admin', '123', 'Админ'),
       ('user', '123', 'Пользователь');

CREATE TABLE authorities
(
    username  varchar(50) NOT NULL,
    authority varchar(50) NOT NULL,

    CONSTRAINT authorities_idx UNIQUE (username, authority),

    CONSTRAINT authorities_ibfk_1
        FOREIGN KEY (username)
            REFERENCES users (username)
);

INSERT INTO authority
VALUES (1, 'ROLE_ADMIN', 'admin'),
       (2, 'ROLE_USER', 'user');

create table Post
(
    post_id  bigserial primary key,
    post_text  varchar(50) not null,
    created_at timestamp not null,
    views int not null default 0,
    username varchar,
        CONSTRAINT authorities_ibfk
        FOREIGN KEY (username)
        REFERENCES users (username)
);

alter table Post
    owner to postgres;

INSERT INTO Post
VALUES (default, '2023-05-05 10:25:31', 'Текст 1', default, 'admin'),
       (default,  '2023-05-15 12:14:05', 'Текст 2', default, 'admin'),
       (default,  '2023-05-30 13:02:14','Текст 3',  default, 'user');



-- CREATE TABLE User_
-- (
--     username  VARCHAR(30) NOT NULL PRIMARY KEY,
--     real_name VARCHAR(30) NOT NULL,
--     password  VARCHAR     NOT NULL
-- );
--
-- alter table User_
--     owner to postgres;
--
-- INSERT INTO User_
-- VALUES ('user1', 'Админ', '1'),
--        ('user2', 'Даниил', '1');
-- CREATE TABLE Authority
-- (
--     username  varchar(50) NOT NULL,
--     authority varchar(50) NOT NULL,
--     UNIQUE (username, authority),
--
--     CONSTRAINT authorities_ibfk
--         FOREIGN KEY (username)
--             REFERENCES user_ (username)
-- );
--
-- INSERT INTO Authority
-- VALUES ('user1', 'ROLE_ADMIN'),
--        ('user1', 'ROLE_USER'),
--        ('user2', 'ROLE_USER');
