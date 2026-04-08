-- Insert Zones
INSERT INTO zone (code, name, risk_coefficient) VALUES
('TN-TUN', 'Tunis', 1.0),
('TN-SFX', 'Sfax', 1.15),
('TN-SUS', 'Sousse', 1.08);

-- Insert Products
INSERT INTO product (name, description) VALUES
('Auto Insurance', 'Comprehensive automobile insurance coverage'),
('Habitation Insurance', 'Home and property insurance protection'),
('Santé Insurance', 'Health and medical insurance coverage');

-- Insert Pricing Rules
-- Auto Insurance
INSERT INTO pricing_rule (product_id, base_rate, age_factor_young, age_factor_adult, age_factor_senior, age_factor_elderly)
VALUES (1, 500.00, 1.3, 1.0, 1.2, 1.5);

-- Habitation Insurance
INSERT INTO pricing_rule (product_id, base_rate, age_factor_young, age_factor_adult, age_factor_senior, age_factor_elderly)
VALUES (2, 800.00, 1.1, 1.0, 1.05, 1.2);

-- Santé Insurance
INSERT INTO pricing_rule (product_id, base_rate, age_factor_young, age_factor_adult, age_factor_senior, age_factor_elderly)
VALUES (3, 1200.00, 1.0, 1.0, 1.3, 1.8);
