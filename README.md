# SYGEP

Systeme de Gestion et d'Evaluation des Projets Academiques avec IA.

## Description

SYGEP centralise le cycle de vie complet d'un projet academique: soumission, validation, suivi, evaluation et archivage. L'application suit une architecture multi-tiers Jakarta EE et prepare l'integration d'une assistance intelligente cote serveur pour l'analyse de qualite, la detection de similarite et l'aide a l'evaluation.

## Repartition des roles

- Andy = Module A: Proposition & Validation
- Dahl = Module B: Supervision & Evaluation

## Workflow resume

Soumission -> validation -> suivi -> evaluation -> archivage

## Stack technique

- Jakarta EE 10
- GlassFish 7+
- JPA / EclipseLink
- JSP / JSTL
- Maven WAR

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

## Branches Git prevues

- `main` : stable
- `develop` : integration
- `moduleA-proposition-validation` : travail Andy
- `moduleB-supervision-evaluation` : travail Dahl

## Demarrage rapide

1. Creer la ressource GlassFish a partir de `src/main/setup/glassfish-resources.xml`
2. Construire le WAR avec `mvn clean package`
3. Deployer `target/sygep.war` sur GlassFish 7+

## Fondation livree

- Projet Maven WAR `sygep`
- `persistence.xml` et `web.xml` preconfigures
- Datasource JNDI `jdbc/sygepDS`
- Entites de base `User` et `Project`
- EJB `AdminService`
- Servlets `AuthServlet`, `ProposalServlet`, `SupervisorServlet`
- Filtre `RoleFilter`
