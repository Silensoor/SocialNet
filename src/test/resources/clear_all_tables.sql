CREATE OR REPLACE FUNCTION clear_all_tables()
  RETURNS void
  LANGUAGE plpgsql AS
$func$
BEGIN
  EXECUTE (
    SELECT 'TRUNCATE TABLE '
           || string_agg(format('%I.%I', schemaname, tablename), ', ')
           || ' CASCADE'
      FROM pg_tables
     WHERE schemaname = 'public'
  );
END
$func$;

SELECT clear_all_tables();
