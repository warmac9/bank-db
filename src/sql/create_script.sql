DROP TABLE IF EXISTS "statement";
DROP TABLE IF EXISTS "card";
DROP TABLE IF EXISTS "transaction";
DROP TABLE IF EXISTS "account";
DROP TABLE IF EXISTS "user";
DROP TABLE IF EXISTS "exchange_rate";
DROP TABLE IF EXISTS "currency";


CREATE TABLE "user" (
    "id" SERIAL PRIMARY KEY,
    "full_name" varchar,
    "email" varchar,
    "id_number" varchar, -- rodne cislo
    "is_deactivated" bool
);

CREATE TABLE "card" (
    "id" SERIAL PRIMARY KEY,
    "account_id" int,
    "number" varchar,
    "cvv" int,
    "valid_to" date
);

CREATE TABLE "account" (
   "id" SERIAL PRIMARY KEY,
   "user_id" int,
   "type" int,
   "currency" varchar,
   "iban" varchar,
   "is_deactivated" bool,
   "activated_at" date,
   "deactivated_at" date,
   "saving_account_id" int,
   "interest_rate" numeric,
   "interest_to" date, -- datum skoncenia terminovaneho uctu
   "balance" numeric
);

CREATE TABLE "transaction" (
   "id" SERIAL PRIMARY KEY,
   "account_id" int,
   "type" int,
   "iban_from" varchar,
   "iban_to" varchar,
   "amount" numeric,
   "currency" varchar,
   "amount_account" numeric,
   "fee" numeric,
   "balance" numeric,
   "created_at" date,
   "executed_at" date
);

CREATE TABLE "currency" (
    "id" SERIAL PRIMARY KEY,
    "code" varchar UNIQUE
);

CREATE TABLE "exchange_rate" (
     "id" SERIAL PRIMARY KEY,
     "currency_from" varchar,
     "currency_to" varchar,
     "amount" numeric
);

CREATE TABLE "statement" (
     "id" SERIAL PRIMARY KEY,
     "user_id" int,
     "text_document" varchar,
     "created_at" date
);

ALTER TABLE "account" ADD FOREIGN KEY ("user_id") REFERENCES "user" ("id");

ALTER TABLE "card" ADD FOREIGN KEY ("account_id") REFERENCES "account" ("id");

ALTER TABLE "account" ADD FOREIGN KEY ("saving_account_id") REFERENCES "account" ("id");

ALTER TABLE "transaction" ADD FOREIGN KEY ("account_id") REFERENCES "account" ("id");

ALTER TABLE "transaction" ADD FOREIGN KEY ("currency") REFERENCES "currency" ("code");

ALTER TABLE "account" ADD FOREIGN KEY ("currency") REFERENCES "currency" ("code");

ALTER TABLE "statement" ADD FOREIGN KEY ("user_id") REFERENCES "user" ("id");
