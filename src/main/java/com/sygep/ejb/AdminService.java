package com.sygep.ejb;

import com.sygep.entity.Project;
import com.sygep.entity.ProjectStatus;
import com.sygep.entity.Role;
import com.sygep.entity.User;
import com.sygep.util.PasswordUtil;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Locale;

@Stateless
public class AdminService {

    @PersistenceContext(unitName = "sygepPU")
    private EntityManager entityManager;

    public List<Project> findAllProjects() {
        List<Project> projects = entityManager.createNamedQuery("Project.findAllOrdered", Project.class)
                .getResultList();
        projects.forEach(this::initializeProjectSummary);
        return projects;
    }

    public List<Project> findProjectsForValidation() {
        return findProjectsForValidation(null);
    }

    public List<Project> findProjectsForValidation(ProjectStatus filter) {
        String jpql = "SELECT p FROM SygepProject p "
                + (filter == null
                        ? "WHERE p.statut IN :statuses "
                        : "WHERE p.statut = :status ")
                + "ORDER BY p.submittedAt ASC, p.createdAt ASC";
        jakarta.persistence.TypedQuery<Project> query = entityManager.createQuery(jpql, Project.class);
        if (filter == null) {
            query.setParameter("statuses", List.of(ProjectStatus.SUBMITTED, ProjectStatus.VALIDATED, ProjectStatus.REJECTED));
        } else {
            query.setParameter("status", filter);
        }
        List<Project> projects = query.getResultList();
        projects.forEach(this::initializeProjectSummary);
        return projects;
    }

    public List<User> findSupervisors() {
        return entityManager.createQuery(
                        "SELECT u FROM SygepUser u WHERE u.role = :role AND u.actif = TRUE ORDER BY u.fullName, u.email",
                        User.class)
                .setParameter("role", Role.SUPERVISOR)
                .getResultList();
    }

    public List<User> findUsers() {
        return entityManager.createQuery("SELECT u FROM SygepUser u ORDER BY u.role, u.fullName, u.email", User.class)
                .getResultList();
    }

    public User createUser(String fullName, String email, String motDePasse, Role role) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("L'email est obligatoire.");
        }
        if (motDePasse == null || motDePasse.isBlank()) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire.");
        }
        if (role == null) {
            throw new IllegalArgumentException("Le role est obligatoire.");
        }
        if (findByEmail(email) != null) {
            throw new IllegalStateException("Un utilisateur existe deja avec cet email.");
        }

        User user = new User(
                fullName,
                email.trim().toLowerCase(Locale.ROOT),
                PasswordUtil.hash(motDePasse),
                role,
                true
        );
        entityManager.persist(user);
        return user;
    }

    public User toggleActive(Long userId) {
        User user = entityManager.find(User.class, userId);
        if (user == null) {
            throw new IllegalArgumentException("Utilisateur introuvable.");
        }

        user.setActif(!user.isActif());
        return user;
    }

    public Project validateProject(Long projectId, Long supervisorId) {
        Project project = requireProject(projectId);
        if (project.getStatut() == ProjectStatus.ARCHIVED) {
            throw new IllegalStateException("Un projet archive ne peut plus etre valide.");
        }

        if (supervisorId != null) {
            User supervisor = requireSupervisor(supervisorId);
            project.setSupervisor(supervisor);
            project.setStatut(ProjectStatus.IN_PROGRESS);
        } else {
            project.setStatut(ProjectStatus.VALIDATED);
        }
        return project;
    }

    public Project rejectProject(Long projectId) {
        Project project = requireProject(projectId);
        if (project.getStatut() == ProjectStatus.ARCHIVED) {
            throw new IllegalStateException("Un projet archive ne peut plus etre rejete.");
        }
        project.setStatut(ProjectStatus.REJECTED);
        return project;
    }

    public Project assignSupervisor(Long projectId, Long supervisorId) {
        Project project = requireProject(projectId);
        User supervisor = requireSupervisor(supervisorId);
        project.setSupervisor(supervisor);
        if (project.getStatut() == ProjectStatus.VALIDATED || project.getStatut() == ProjectStatus.SUBMITTED) {
            project.setStatut(ProjectStatus.IN_PROGRESS);
        }
        return project;
    }

    private Project requireProject(Long projectId) {
        Project project = projectId == null ? null : entityManager.find(Project.class, projectId);
        if (project == null) {
            throw new IllegalArgumentException("Projet introuvable.");
        }
        return project;
    }

    private User requireSupervisor(Long supervisorId) {
        User supervisor = supervisorId == null ? null : entityManager.find(User.class, supervisorId);
        if (supervisor == null || supervisor.getRole() != Role.SUPERVISOR || !supervisor.isActif()) {
            throw new IllegalArgumentException("Superviseur introuvable ou inactif.");
        }
        return supervisor;
    }

    private User findByEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }

        List<User> results = entityManager.createQuery(
                        "SELECT u FROM SygepUser u WHERE LOWER(u.email) = LOWER(:email)", User.class)
                .setParameter("email", email.trim())
                .setMaxResults(1)
                .getResultList();

        return results.isEmpty() ? null : results.get(0);
    }

    private void initializeProjectSummary(Project project) {
        project.getStudent().getEmail();
        if (project.getSupervisor() != null) {
            project.getSupervisor().getEmail();
        }
        if (project.getEvaluation() != null) {
            project.getEvaluation().getFinalScore();
        }
    }
}
