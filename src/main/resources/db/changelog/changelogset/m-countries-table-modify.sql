alter table Countries
	drop column if exists internationalname;

alter table Countries
	add column if not exists international_name character varying(255);
