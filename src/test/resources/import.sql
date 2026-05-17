TRUNCATE TABLE product;

ALTER TABLE product ALTER COLUMN id RESTART WITH 1;

INSERT INTO product (name, price, stock) VALUES ('Product1', 100.0, 10);
INSERT INTO product (name, price, stock) VALUES ('Product2', 200.0, 10);