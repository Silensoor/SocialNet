drop table person_settings;

CREATE TABLE person_settings
(
    id bigint NOT NULL,
    comment_comment boolean NOT NULL default false,
    friend_birthday boolean NOT NULL default false,
    friend_request boolean not null default false,
    post_like boolean NOT NULL default false,
    message boolean NOT NULL default false,
    post_comment boolean NOT NULL default false,
    post boolean NOT NULL default false,
    CONSTRAINT person_settings_key PRIMARY KEY (id),
    CONSTRAINT fk_persons FOREIGN KEY (id)
        REFERENCES public.persons (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);