# Test Technique — Full Stack Engineer — Tendanz Group

## Moteur de Tarification Assurance

### Contexte

Vous rejoignez une équipe projet chez Tendanz Group pour développer un **moteur de tarification d'assurance**. Le système doit permettre de calculer le prix d'une couverture en fonction du profil client, du produit choisi et de la zone géographique.

### Structure du projet

```
├── backend/          # Spring Boot 3.2 — Java 17
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/tendanz/pricing/
│       │   ├── controller/    # → À implémenter (TODO)
│       │   ├── service/       # → À implémenter (TODO)
│       │   ├── repository/    # QuoteRepository → À compléter
│       │   ├── entity/        # Entités JPA (fournies)
│       │   ├── dto/           # DTOs (fournis)
│       │   ├── exception/     # → À implémenter (TODO)
│       │   └── enums/         # AgeCategory (fourni)
│       └── main/resources/
│           ├── schema.sql     # DDL (fourni)
│           ├── data.sql       # Données initiales (fourni)
│           └── application.yml
│
└── frontend/         # Angular 17 — Standalone Components
    ├── package.json
    └── src/app/
        ├── services/      # → À implémenter (TODO)
        ├── pages/         # → À implémenter (TODO)
        └── models/        # Interfaces TypeScript (fournies)
```

### Démarrage rapide

**Backend :**
```bash
cd backend
mvn spring-boot:run
# API disponible sur http://localhost:8080
```

**Frontend :**
```bash
cd frontend
npm install
ng serve
# App disponible sur http://localhost:4200
```

### Formule de tarification

```
Prix Final = Taux de Base × Facteur Âge × Coefficient Zone
```

Voir l'énoncé complet dans le document Word fourni.

### Livrable attendu

- Code complété (tous les fichiers `TODO`)
- Minimum 5 tests unitaires backend
- README mis à jour avec vos choix techniques
- Commits Git propres et progressifs

### Délai : 48 heures

Bonne chance !
