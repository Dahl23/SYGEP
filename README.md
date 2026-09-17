# SYGEP

Systeme de Gestion et d'Evaluation des Projets Academiques.

## Description

SYGEP centralise le cycle de vie complet d'un projet academique : soumission, validation, suivi, evaluation et archivage. L'application suit une architecture multi-tiers Jakarta EE.

## Workflow resume

Soumission -> validation -> suivi -> evaluation -> archivage

## Stack technique

- Jakarta EE 10
- GlassFish 7+
- JPA / EclipseLink
- JSP / JSTL
- Maven WAR
- PostgreSQL

## Structure du projet

```text
src/main/java
src/main/resources
src/main/setup
src/main/webapp
```

## Configuration JPA et datasource

- Persistence unit: `sygepPU`
- Provider: `org.eclipse.persistence.jpa.PersistenceProvider`
- Datasource JNDI: `jdbc/sygepDS`
- Fichier GlassFish: `src/main/setup/glassfish-resources.xml`

Le fichier `glassfish-resources.xml` est preconfigure avec un pool PostgreSQL local de demonstration. Adaptez les identifiants et ajoutez le driver JDBC dans GlassFish avant de deployer.

## Demarrage rapide

1. Creer la base PostgreSQL `sygep`.
2. Adapter les identifiants dans `src/main/setup/glassfish-resources.xml`.
3. Creer la ressource GlassFish a partir de `src/main/setup/glassfish-resources.xml`.
4. Construire le WAR avec `mvn clean package`.
5. Deployer `target/sygep.war` sur GlassFish 7+.

EclipseLink cree ou etend automatiquement les tables avec `eclipselink.ddl-generation=create-or-extend-tables`.

## Comptes de demonstration

Les comptes suivants sont crees automatiquement au premier acces a `/login` si absents :

- `admin@sygep.local` / `admin123`
- `student@sygep.local` / `student123`
- `supervisor@sygep.local` / `supervisor123`

## Fonctionnalites livrees

- Authentification, logout et filtrage par roles `ADMIN`, `STUDENT`, `SUPERVISOR`.
- Soumission, modification et consultation des projets.
- Depot d'un document PDF ou Word (10 Mo max) avec chaque projet ; les superviseurs et l'administrateur peuvent le telecharger et le lire.
- Validation, rejet et affectation de superviseur par l'administrateur.
- Gestion des utilisateurs par l'administrateur (creation, activation/desactivation).
- Ajout de commentaires et rapports de suivi par le superviseur.
- Edition et suppression des commentaires et rapports de suivi par le superviseur.
- Evaluation technique / documentation / presentation avec calcul automatique de la note finale.
- Re-evaluation d'un projet deja archive avec mise a jour de la note finale.
- Historique des evaluations effectuees par le superviseur.
- Archivage automatique apres evaluation.
- Listes paginees sur le tableau de bord superviseur.

## Routes principales

- `/login`, `/logout`
- `/dashboard`
- `/projects`
- `/project/view`, `/project/download`
- `/proposal/new`, `/proposal/edit`, `/proposal/submit`, `/proposal/update`
- `/admin/projects`, `/admin/validate`, `/admin/reject`, `/admin/assign`, `/admin/users/*`
- `/supervisor/dashboard`, `/supervisor/comment`, `/supervisor/progress`
- `/evaluation/new`, `/evaluation/save`