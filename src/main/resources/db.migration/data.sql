-- Données pour l'entité Profile
INSERT INTO profile (id, user_id, nom, prenom, telephone, ville, pays, date_naissance, date_mort) VALUES
(1, 2, 'Doe', 'John', '123456789', 'Paris', 'France', 1969-09-01,2019-02-21),
(2, 3, 'Admin', 'Super', '987654321', 'Lyon', 'France', 1940-09-01,2010-02-21);

-- Données pour l'entité ParcoursAcademique
INSERT INTO parcours_academique (id, profile_id, institution, diplome, specialite, date_debut, date_fin) VALUES
(1, 1, 'Université de Paris', 'Licence', 'Informatique', '2010-09-01', '2013-06-30'),
(2, 1, 'Université de Lyon', 'Master', 'Data Science', '2013-09-01', '2015-06-30');

-- Données pour l'entité ExperienceProfessionnelle
INSERT INTO experience_professionnelle (id, profile_id, entreprise, poste, date_debut, date_fin) VALUES
(1, 1, 'Tech Corp', 'Développeur', '2015-07-01', '2018-08-30'),
(2, 1, 'Data Solutions', 'Ingénieur Data', '2018-09-01', '2022-05-31');

-- Données pour l'entité RelationFamille
INSERT INTO relation_famille (id, profile_id, relative_id, relation_type) VALUES
(1, 1, 2, 'PARENT');

-- Données pour l'élément collection des contacts dans Profile
INSERT INTO profile_contacts (profile_id, contacts) VALUES
(1, 'contact1@example.com'),
(1, 'contact2@example.com'),
(2, 'admin@example.com');
