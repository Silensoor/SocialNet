CREATE OR REPLACE FUNCTION new_city_function()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$
declare country bigint;
BEGIN
	new.country_id = (Select id from countries where code2 = new.code2);
	return new;
END;
$function$
;

create or replace trigger insert_city
before insert
on cities
for each row execute function new_city_function();
