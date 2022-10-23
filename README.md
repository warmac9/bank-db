## Databázový model
![image](https://user-images.githubusercontent.com/19654413/197392745-9fdeca37-d5f8-4bd3-9bee-d410b97891cd.png)

### Vysvetlenie:
  Banka (Obr. 1) má užívateľov (zákazníkov) uložených v množine **user**. Jeden zákazník môže mať viacero 
účtov. Účty zákazníka sú uložené v množine **account**. Nepoužil som ďalšiu množinu pre zachytenie vzťahu medzi 
množinou **user** a **account**, tým, že neuvažujem s viacerými disponentmi účtu a každý účet je naviazaný len na 
jedného zákazníka. Časový vývoj jednotlivých sadzieb (interest rate a exchange rate) sledujem v samostatných 
tabuľkách a používam pre výpočet v čase zúčtovania danej transakcie.

  Nakoľko vlastnosti jednotlivých účtov sú veľmi podobné, rozhodol som sa pre možnosť, kde množinu **account**
budú používať všetky typy účtov. Vďaka tomu množiny, ktoré súvisia s účtom by neobsahovali duplikované polia
v samostatných tabuľkách. Množina **account** je použitá pre správu všetkých typov účtov. Pole type v množine
**account** symbolizuje typ účtu, či je bežný, sporiaci alebo terminovaný. K typom účtu používam samostatnú tabuľku 
**interest_rate** obsahujúce polia typ účtu, výšku úroku a dátum platnosti úroku od-do. Pole interest_to v množine 
**account** obsahuje dátum dokedy sa úročia peniaze a akú dobu je termínovaný účet viazaný. Pole 
saving_account_id odkazuje na práve jeden viazaný bežný účet v prípade sporiaceho účtu. V transakcii rozlišujem 
sumu transakcie v mene transakcie amount a v mene účtu amount_account. V poli balance je uložený zostatok 
účtu, ktorý je vyjadrený v mene účtu.

  Rôzne typy transakcií som sa rozhodol uložiť do jednej množiny **transaction**, nakoľko spolu úzko súvisia a 
vďaka čomu bude databáza prehľadnejšia a lepšie udržiavateľná. Zároveň stačí dopyt po jednej tabuľke, aby sa 
získali potrebné informácie ohľadom transakcií. Pole executed_at symbolizuje s akým dátumom sa transakcia 
zrealizovala. Pole type nesie v sebe informáciu ohľadom typu transakcie, či sa jedná o vklad, výber, prevod, úrok
atď. Poplatky som ponechal ako súčasť množiny **transaction** vypočítavanú v poli fee. Spôsob výpočtu výšku 
poplatkov na transakciách je uložený v množine **transaction_rate**, obsahuje fixnú sumu v poli fixed_fee alebo 
percentuálnu v poli relative_fee, pole free_transactions by obsahovalo počet nespoplatnených transakcií 
v jednom mesiaci. V množine **transaction** sa taktiež nachádza výsledný stav účtu po zrealizovaní transakcie v poli 
balance. Transakcie sú naviazané cez pole account_id avšak počítajú aj s prevodom z/na účty iných bánk cez polia 
iban_from resp. iban_to.

  Pole id_number predstavuje rodné číslo. Množina **user** a **account** obsahuje pole is_deactivated, vďaka
čomu, keď deaktivujem účet nám dáta ohľadom zákazníka zostanú v databáze. Nevymažeme účet, resp. užívateľa
úplne, len naznačíme, že je vymazaný tzv. soft delete. Množina **account** obsahuje navyše pole activated_at a 
deactivated_at, aby bolo možné monitorovať počet znovuzískaných zákazníkov.

  Prevodové kurzy sú uložené v množine exchange_rate a meny v množine currency. Pole code obsahuje 
skratku meny. Pole amount vyjadruje kurz daných mien v danom období.
