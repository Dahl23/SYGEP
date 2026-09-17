package com.sygep.ejb;

import com.sygep.entity.Evaluation;
import com.sygep.entity.Project;
import com.sygep.entity.ProjectStatus;
import com.sygep.entity.Role;
import com.sygep.entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class EvaluationService {

    private static final BigDecimal MIN_SCORE = BigDecimal.ZERO;
    private static final BigDecimal MAX_SCORE = new BigDecimal("20");

    @PersistenceContext(unitName = "sygepPU")
    private EntityManager entityManager;

    public List<Project> findProjectsReadyForEvaluation(Long supervisorId) {
        List<Project> projects = entityManager.createQuery(
                        "SELECT p FROM SygepProject p "
                                + "WHERE p.supervisor.id = :supervisorId "
                                + "AND p.statut IN :statuses "
                                + "ORDER BY p.updatedAt DESC",
                        Project.class)
                .setParameter("supervisorId", supervisorId)
                .setParameter("statuses", List.of(ProjectStatus.IN_PROGRESS, ProjectStatus.VALIDATED))
                .getResultList();
        projects.forEach(this::initializeProjectSummary);
        return projects;
    }

    public Project findProjectForEvaluation(Long projectId, Long evaluatorId) {
        Project project = requireProject(projectId);
        User evaluator = requireEvaluator(evaluatorId);
        ensureEvaluationAccess(project, evaluator);
        initializeProjectDetails(project);
        return project;
    }

    public List<Project> findEvaluatedProjectsByEvaluator(Long evaluatorId) {
        List<Project> projects = entityManager.createQuery(
                        "SELECT p FROM SygepProject p "
                                + "WHERE p.statut IN :statuses "
                                + "AND EXISTS (SELECT e FROM SygepEvaluation e WHERE e.project = p AND e.evaluator.id = :evaluatorId) "
                                + "ORDER BY p.updatedAt DESC",
                        Project.class)
                .setParameter("statuses", List.of(ProjectStatus.EVALUATED, ProjectStatus.ARCHIVED))
                .setParameter("evaluatorId", evaluatorId)
                .getResultList();
        projects.forEach(this::initializeProjectSummary);
        return projects;
    }

    public Evaluation reEvaluateProject(Long projectId, Long evaluatorId, BigDecimal technicalScore,
                                        BigDecimal documentationScore, BigDecimal presentationScore,
                                        String feedback) {
        Project project = requireProject(projectId);
        User evaluator = requireEvaluator(evaluatorId);

        Evaluation evaluation = findEvaluationByProject(projectId);
        if (evaluation == null) {
            throw new IllegalStateException("Aucune evaluation existante. Utilisez la premiere evaluation.");
        }
        ensureEvaluationAccess(project, evaluator);

        evaluation.setEvaluator(evaluator);
        evaluation.setTechnicalScore(normalizeScore(technicalScore, "Note technique"));
        evaluation.setDocumentationScore(normalizeScore(documentationScore, "Note documentation"));
        evaluation.setPresentationScore(normalizeScore(presentationScore, "Note presentation"));
        evaluation.setFeedback(feedback == null || feedback.isBlank() ? null : feedback.trim());
        evaluation.setEvaluatedAt(LocalDateTime.now());
        evaluation.calculateFinalScore();
        project.setStatut(ProjectStatus.ARCHIVED);
        return evaluation;
    }

    public Evaluation evaluateProject(Long projectId, Long evaluatorId, BigDecimal technicalScore,
                                      BigDecimal documentationScore, BigDecimal presentationScore,
                                      String feedback) {
        Project project = requireProject(projectId);
        User evaluator = requireEvaluator(evaluatorId);
        ensureEvaluationAccess(project, evaluator);

        Evaluation evaluation = findEvaluationByProject(projectId);
        boolean newEvaluation = evaluation == null;
        if (newEvaluation) {
            evaluation = new Evaluation();
            evaluation.setProject(project);
            project.setEvaluation(evaluation);
        }

        evaluation.setEvaluator(evaluator);
        evaluation.setTechnicalScore(normalizeScore(technicalScore, "Note technique"));
        evaluation.setDocumentationScore(normalizeScore(documentationScore, "Note documentation"));
        evaluation.setPresentationScore(normalizeScore(presentationScore, "Note presentation"));
        evaluation.setFeedback(feedback == null || feedback.isBlank() ? null : feedback.trim());
        evaluation.setEvaluatedAt(LocalDateTime.now());
        evaluation.calculateFinalScore();

        if (newEvaluation) {
            entityManager.persist(evaluation);
        }

        project.setStatut(ProjectStatus.ARCHIVED);
        return evaluation;
    }

    public Evaluation findEvaluationByProject(Long projectId) {
        List<Evaluation> evaluations = entityManager.createQuery(
                        "SELECT e FROM SygepEvaluation e WHERE e.project.id = :projectId",
                        Evaluation.class)
                .setParameter("projectId", projectId)
                .setMaxResults(1)
                .getResultList();
        return evaluations.isEmpty() ? null : evaluations.get(0);
    }

    private Project requireProject(Long projectId) {
        Project project = projectId == null ? null : entityManager.find(Project.class, projectId);
        if (project == null) {
            throw new IllegalArgumentException("Projet introuvable.");
        }
        return project;
    }

    private User requireEvaluator(Long evaluatorId) {
        User evaluator = evaluatorId == null ? null : entityManager.find(User.class, evaluatorId);
        if (evaluator == null || !evaluator.isActif()) {
            throw new IllegalArgumentException("Evaluateur introuvable ou inactif.");
        }
        if (evaluator.getRole() != Role.SUPERVISOR && evaluator.getRole() != Role.ADMIN) {
            throw new SecurityException("Seul un superviseur ou un administrateur peut evaluer.");
        }
        return evaluator;
    }

    private void ensureEvaluationAccess(Project project, User evaluator) {
        if (project.getStatut() == ProjectStatus.SUBMITTED || project.getStatut() == ProjectStatus.REJECTED) {
            throw new IllegalStateException("Ce projet doit etre valide avant evaluation.");
        }
        if (project.getStatut() == ProjectStatus.ARCHIVED && findEvaluationByProject(project.getId()) == null) {
            throw new IllegalStateException("Ce projet est deja archive sans evaluation.");
        }
        if (evaluator.getRole() == Role.ADMIN) {
            return;
        }
        if (project.getSupervisor() == null || !project.getSupervisor().getId().equals(evaluator.getId())) {
            throw new SecurityException("Ce projet n'est pas affecte a ce superviseur.");
        }
    }

    private BigDecimal normalizeScore(BigDecimal score, String label) {
        if (score == null) {
            throw new IllegalArgumentException(label + " obligatoire.");
        }
        if (score.compareTo(MIN_SCORE) < 0 || score.compareTo(MAX_SCORE) > 0) {
            throw new IllegalArgumentException(label + " doit etre comprise entre 0 et 20.");
        }
        return score;
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

    private void initializeProjectDetails(Project project) {
        initializeProjectSummary(project);
        project.getSupervisorComments().size();
        project.getProgressReports().size();
    }
}
