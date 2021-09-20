# --- !Ups

INSERT INTO "category"(name)
VALUES ('books');
INSERT INTO "category"(name)
VALUES ('furniture');
INSERT INTO "category"(name)
VALUES ('food');
INSERT INTO "category"(name)
VALUES ('toys');

INSERT INTO "product"(name, description, category)
VALUES ('Book1', 'BookDescription1',
        (SELECT id FROM category WHERE name = 'books'));
INSERT INTO "product"(name, description, category)
VALUES ('Furniture1', 'FurnitureDescription1',
        (SELECT id FROM category WHERE name = 'furniture'));
INSERT INTO "product"(name, description, category)
VALUES ('Book2', 'BookDescription2',
        (SELECT id FROM category WHERE name = 'books'));
INSERT INTO "product"(name, description, category)
VALUES ('Food1', 'FoodDescription1',
        (SELECT id FROM category WHERE name = 'food'));
INSERT INTO "product"(name, description, category)
VALUES ('Toy1', 'ToyDescription1',
        (SELECT id FROM category WHERE name = 'toys'));
INSERT INTO "product"(name, description, category)
VALUES ('Toy2', 'ToyDescription2',
        (SELECT id FROM category WHERE name = 'toys'));
INSERT INTO "product"(name, description, category)
VALUES ('Food2', 'FoodDescription2',
        (SELECT id FROM category WHERE name = 'food'));
INSERT INTO "product"(name, description, category)
VALUES ('Furniture2', 'FurnitureDescription2',
        (SELECT id FROM category WHERE name = 'furniture'));

INSERT INTO "user"(email)
VALUES ('ola@gmail.com');

# --- !Downs

DELETE
FROM user
WHERE email = 'ola@gmail.com';

DELETE
FROM product
WHERE name = 'Furniture2';
DELETE
FROM product
WHERE name = 'Food2';
DELETE
FROM product
WHERE name = 'Toy2';
DELETE
FROM product
WHERE name = 'Toy1';
DELETE
FROM product
WHERE name = 'Food1';
DELETE
FROM product
WHERE name = 'Book2';
DELETE
FROM product
WHERE name = 'Furniture1';
DELETE
FROM product
WHERE name = 'Book1';

DELETE
FROM category
WHERE name = 'toys';
DELETE
FROM category
WHERE name = 'food';
DELETE
FROM category
WHERE name = 'furniture';
DELETE
FROM category
WHERE name = 'books';