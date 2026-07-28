-- ====================================================================
-- UTENTE 1: AMMINISTRATORE (ADMIN)
-- ====================================================================
-- Usiamo la sequenza corretta per gli utenti
INSERT INTO users (id, nome, cognome, email)  VALUES (nextval('users_seq'), 'Admin', 'Admin', 'admin@fitness.com');

-- Inseriamo il riferimento user_id prendendo il valore corrente di users_seq
INSERT INTO credentials (id, username, password, role, user_id)  VALUES (nextval('credentials_seq'), 'admin', '$2a$10$bsMuG/Mrk4FbCausZOSupuVFgCSzHsijqBAmDKDYy.UGXZJN71lsG', 'ADMIN', currval('users_seq'));


-- ====================================================================
-- UTENTE 2: UTENTE STANDARD (DEFAULT)
-- ====================================================================
-- Usiamo la sequenza corretta per gli utenti
INSERT INTO users (id, nome, cognome, email)  VALUES (nextval('users_seq'), 'User', 'User', 'user@fitness.com');

-- Colleghiamo le credenziali compilando la colonna user_id
INSERT INTO credentials (id, username, password, role, user_id)  VALUES (nextval('credentials_seq'), 'user', '$2a$10$12nR4DQzMS1xih7/R43zXueAj45Rk8uHCp8quu98OJULT9zodqzS2', 'DEFAULT', currval('users_seq'));