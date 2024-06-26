INSERT INTO organisation_legal_forms(name) VALUES
    ('ИП'),
    ('ООО'),
    ('АО'),
    ('Полное товарищество'),
    ('Коммандитное товарищество'),
    ('Производственные кооперативы'),
    ('НКО');

INSERT INTO banks(bik) VALUES
    ('234973478'),
    ('564452234'),
    ('987234967'),
    ('896798563'),
    ('293877548'),
    ('459867984'),
    ('982347569'),
    ('023947594'),
    ('098753497'),
    ('908937475');

INSERT INTO clients(name, shortname, address, organisation_legal_form_id)
VALUES
    ('ЛУКОЙЛ-ПЕРМЬ', 'ЛП', 'Ул. Ленина, дом 50', 1),
    ('ФОРВАРД', 'ФРВРД', 'Проспект Комсомольский, дом 78', 4),
    ('УРАЛКАЛИЙ', 'УК', 'Ул. Мира, дом 12', 7),
    ('ЭР-ТЕЛЕКОМ ХОЛДИНГ', 'ЭРТ', 'Ул. Советская, дом 20', 2),
    ('КАМА', 'Км', 'Ул. Газеты "Звезда", дом 35', 3),
    ('ГУБАХИНСКИЙ КОКС', 'ГКОКС', 'Ул. Революции, дом 5', 5),
    ('Нет блин пермский мефедрон', 'НБПМ', 'Ул. Попова, дом 15', 4),
    ('ЮКМП', 'как тут сократить', 'Ул. Плеханова, дом 22', 5);

INSERT INTO deposits(client_id, bank_id, open_date, percent, retention_period)
VALUES
    (1, 1, '2005-09-20', 6.5, 20),
    (4, 8, '2002-01-25', 10, 10),
    (2, 3, '2012-12-31', 12, 15),
    (7, 2, '2023-03-24', 5, 4),
    (3, 1, '2022-08-26', 8.4, 36),
    (5, 6, '2021-07-01', 9.2, 8),
    (2, 4, '2020-09-17', 10.5, 120),
    (3, 3, '2008-01-20', 12.5, 60);

