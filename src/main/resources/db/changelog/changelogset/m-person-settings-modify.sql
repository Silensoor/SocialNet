drop table person_settings;

CREATE TABLE person_settings
(
    id bigint NOT NULL,
    comment_comment_notification boolean NOT NULL default false,
    friend_birthday_notification boolean NOT NULL default false,
    like_notification boolean NOT NULL default false,
    message_notification boolean NOT NULL default false,
    post_comment_notification boolean NOT NULL default false,
    post_notification boolean NOT NULL default false,
    friend_request boolean not null default false,
    CONSTRAINT person_settings_key PRIMARY KEY (id),
    CONSTRAINT fk_persons FOREIGN KEY (id)
        REFERENCES public.persons (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
)

--CREATE FUNCTION new_person_function()
--    RETURNS trigger
--    LANGUAGE 'plpgsql'
--as $function$
--begin
--	insert into person_settings (id) values (new.id);
--	return new;
--end;
--$function$;
--
--CREATE TRIGGER new_person_trigger
--    BEFORE INSERT
--    ON public.persons
--    FOR EACH ROW
--    EXECUTE FUNCTION new_person_function();