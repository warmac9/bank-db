truncate table "account", "card", "currency", "exchange_rate", "statement", "transaction", "user" restart identity cascade;


-- zdroj: generate_script.sql v ukazkovom projekte

drop table if exists first_names cascade;
create table first_names
(
    first_name varchar
);

insert into first_names (first_name)
values	('James'), ('Willie'), ('Chad'), ('Zachary'), ('Mathew'),
          ('John'), ('Ralph'), ('Jacob'), ('Corey'), ('Tyrone'),
          ('Robert'), ('Lawrence'), ('Lee'), ('Herman'), ('Darren'),
          ('Michael'), ('Nicholas'), ('Melvin'), ('Maurice'), ('Lonnie'),
          ('William'), ('Roy'), ('Alfred'), ('Vernon'), ('Lance'),
          ('David'), ('Benjamin'), ('Kyle'), ('Roberto'), ('Cody');

-- last names

drop table if exists last_names cascade;
create table last_names
(
    last_name varchar
);

insert into last_names (last_name)
values	('Smith'), ('Jones'), ('Taylor'), ('Williams'), ('Brown'),
          ('Davies'), ('Evans'), ('Wilson'), ('Thomas'), ('Roberts'),
          ('Johnson'), ('Lewis'), ('Walker'), ('Robinson'), ('Wood'),
          ('Thompson'), ('White'), ('Watson'), ('Jackson'), ('Wright'),
          ('Green'), ('Harris'), ('Cooper'), ('King'), ('Lee'),
          ('Martin'), ('Clarke'), ('James'), ('Morgan'), ('Hughes'),
          ('Edwards'), ('Hill'), ('Moore'), ('Clark'), ('Harrison'),
          ('Scott'), ('Young'), ('Morris'), ('Hall'), ('Ward'),
          ('Turner'), ('Carter'), ('Phillips'), ('Mitchell'), ('Patel'),
          ('Adams'), ('Campbell'), ('Anderson'), ('Allen'), ('Cook');


create or replace function random_first_name() returns varchar language sql as
$$
select first_name from first_names order by random() limit 1
$$;

create or replace function random_last_name() returns varchar language sql as
$$
select last_name from last_names order by random() limit 1
$$;

-- --

insert into "currency" (code)
values
    ('EUR'),
    ('USD'),
    ('GBP');


insert into "exchange_rate" (currency_from, currency_to, amount)
values
    ('EUR', 'USD', 1.09),
    ('EUR', 'GBP', 0.83),
    ('USD', 'EUR', 0.91),
    ('USD', 'GBP', 0.76),
    ('GBP', 'EUR', 1.19),
    ('GBP', 'USD', 1.3);


create or replace function random_currency() returns varchar language sql as
$$
select code from currency order by random() limit 1
$$;


insert into "user" (full_name, email, id_number, is_deactivated)
select	random_first_name() || ' ' || random_last_name(),
          random_last_name() || '@gmail.com',
          floor(random()*(999999-100000+1))+100000 || '/' || floor(random()*(9999-1000+1))+1000,
          false
from generate_series(1, 100) as seq(i);

create or replace function random_user() returns int language sql as
$$
select id from "user" order by random() limit 1
$$;


--bezne

insert into "account" (user_id, type, currency, iban, is_deactivated, activated_at, balance)
select	random_user(),
          1,
          random_currency(),
          'SK' || floor(random()*(99-10+1))+10 || ' ' || floor(random()*(9999-1000+1))+1000 || ' ' || floor(random()*(9999-1000+1))+1000,
          false,
          now() - (random()*(interval '3600 days')),
          floor(random()*100000)
from generate_series(1, 100) as seq(i);


--bezne deaktivovane

insert into "account" (user_id, type, currency, iban, is_deactivated, activated_at, deactivated_at, balance)
select	random_user(),
          1,
          random_currency(),
          'SK' || floor(random()*(99-10+1))+10 || ' ' || floor(random()*(9999-1000+1))+1000 || ' ' || floor(random()*(9999-1000+1))+1000,
          true,
          now() - (random()*interval '1200 days') - interval '1200 days',
    now() - (random()*interval '1200 days'),
    floor(random()*100000)
from generate_series(1, 50) as seq(i);


--terminovane

insert into "account" (user_id, type, currency, iban, is_deactivated, activated_at, interest_rate, interest_to, balance)
select	random_user(),
          3,
          random_currency(),
          'SK' || floor(random()*(99-10+1))+10 || ' ' || floor(random()*(9999-1000+1))+1000 || ' ' || floor(random()*(9999-1000+1))+1000,
          false,
          now() - (random()*(interval '1200 days')) - interval '1200 days',
    1.1,
    now() + (random()*(interval '3600 days')) - interval '1200 days',
    floor(random()*100000)
from generate_series(1, 100) as seq(i);


create or replace function random_account(ui int) returns int language sql as
$$
select id from "account" where user_id = ui and "type" = 1 order by random() limit 1
$$;

--sporiace

do $$
declare ru int;
declare ra int;
begin
for j in 1..100 loop
ra := 0;
select random_user() into ru;
select random_account(ru) into ra;
if ra <> 0 then
  insert into "account" (user_id, type, currency, iban, is_deactivated, activated_at, saving_account_id, interest_rate, balance)
select ru,
       2,
       (select currency from account where id=ra limit 1),
                  'SK' || floor(random()*(99-10+1))+10 || ' ' || floor(random()*(9999-1000+1))+1000 || ' ' || floor(random()*(9999-1000+1))+1000,
                  false,
                  now() - (random()*(interval '1200 days')) - interval '1200 days',
                  ra,
                  1.05,
                  floor(random()*100000)
from generate_series(1, 1) as seq(i);
end if;
end loop;
end $$;
