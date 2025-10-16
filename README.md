# 🧬 ms-genealogy – Application de Généalogie

> Une application moderne permettant de **créer, visualiser et partager son arbre généalogique** de manière intuitive et collaborative.

---

## 🌿 Description

**ms-genealogy** est une application de généalogie conçue pour aider les utilisateurs à :
- Créer et gérer leurs **arbres généalogiques**
- Ajouter des **membres de la famille** (liens de parenté, dates, lieux, photos, etc.)
- Visualiser la **structure familiale** sous forme d’arbre ou de graphe
- Partager leur arbre avec d’autres utilisateurs
- Sauvegarder et exporter leurs données généalogiques

L’application combine **simplicité**, **visualisation claire** et **partage collaboratif** entre membres d’une même famille.

---

## 🧩 Fonctionnalités principales

- 👨‍👩‍👧 Gestion complète des membres de la famille  
- 🔍 Recherche intelligente et filtrage  
- 🔗 Connexion des relations familiales (parents, enfants, conjoints)  
- ☁️ Sauvegarde des données et export JSON/PDF  
- 🔐 Authentification sécurisée par JWT 

---

## ⚙️ Architecture du projet

Le projet suit une architecture **backend** claire et modulaire :

```
ms-genealogy/             # API Spring Boot
│   ├── src/
│   │   ├── main/java/com/ms-genealogy/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   └── model/
│   │   │   └── config/
│   │   │   └── dto/
│   │   └── resources/
            └── changelog/
│   └── pom.xml
│
└── README.md
```

> 🧠 Cette structure favorise la **modularité**, la **lisibilité** et la **scalabilité** du projet.

---

## 🛠️ Technologies utilisées

| Catégorie | Technologie |
|------------|-------------|
| Backend | Spring Boot (Java 17+) |
| Base de données | PostgreSQL |
| Mapping | MapStruct |
| Sécurité | Spring Security + JWT |
| Communication API | REST |
| Hébergement, AWS ou Heroku (backend) |

---

## 🚀 Installation & Lancement

### 🔹 1. Cloner le projet
```bash
git clone https://github.com/ton-utilisateur/ms-genealogy.git
cd ms-genealogy
```

---

### 🔹 2. Configuration du backend (Spring Boot)

1. Accéder au dossier backend :
   ```bash
   cd backend
   ```

2. Configurer la base de données PostgreSQL dans `src/main/resources/application.properties` :
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/ms-genealogy_db
   spring.datasource.username=postgres
   spring.datasource.password=ton_mot_de_passe
   spring.jpa.hibernate.ddl-auto=none
   springdoc.api-docs.path=/api-docs
   ```

3. Lancer l’API :
   ```bash
   mvn spring-boot:run
   ```

4. Accéder à l’API :
   👉 [http://localhost:8080](http://localhost:8080)

---

## 🧪 Tests

### Backend :
```bash
mvn test
```

### Frontend :
```bash
npm run test
```

---

## 📚 Documentation API

Une documentation Swagger est disponible à l’adresse suivante :
```
http://localhost:8080/swagger-ui.html
```

---

## 🧠 Roadmap

- [ ] 📱 Version mobile (Flutter) - pas encore commencé!
- [ ] 🔔 Notifications familiales
- [ ] 🗂️ Exportation en format GEDCOM
- [ ] 🧾 Gestion des sources historiques
- [ ] 🤝 Collaboration en temps réel sur l’arbre

---

## 🤝 Contribution

Les contributions sont **les bienvenues** ! 🙌  
Pour contribuer :
1. Fork le projet 🍴  
2. Crée une branche (`feature/ma-fonctionnalite`)  
3. Commit tes changements (`git commit -m "Ajout de ma fonctionnalité"`)  
4. Push la branche  
5. Ouvre une **Pull Request**

---

## 👨‍💻 Auteur

**Vadil Kwekam**  
Développeur Fullstack – Passionné par la conception de logiciel 
📧 [vadilkwekam@gmail.com]  
🌍 [LinkedIn](www.linkedin.com/in/vadil-kwekam-0169a2121)

---

## 📜 Licence

Ce projet est sous licence **MIT**.  
Tu es libre de l’utiliser, le modifier et le distribuer à des fins personnelles ou professionnelles.
