package com.sygep.ejb;

import com.sygep.dto.AIAnalysisResult;
import com.sygep.entity.Evaluation;
import com.sygep.entity.ProgressReport;
import com.sygep.entity.Project;
import com.sygep.entity.ProjectStatus;
import com.sygep.entity.Role;
import com.sygep.entity.SupervisorComment;
import com.sygep.entity.User;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class ProjectService {

    @PersistenceContext(unitName = "sygepPU")
    private EntityManager entityManager;

    @EJB
    private AIService aiService;

    public Project submitProject(Long studentId, String titre, String resume, String keywords) {
        User student = requireUser(studentId, Role.STUDENT);
        validateProjectText(titre, resume);

        Project project = new Project(titre.trim(), resume.trim(), trimToNull(keywords), student);
        applyAI(project);
        entityManager.persist(project);
        return project;
    }

    public Project updateProject(Long projectId, Long studentId, String titre, String resume, String keywords) {
        Project project = requireProject(projectId);
        if (!project.getStudent().getId().equals(studentId)) {
            throw new SecurityException("Ce projet n'appartient pas a l'etudiant connecte.");
        }
        if (!project.isEditableByStudent()) {
            throw new IllegalStateException("Ce projet ne peut plus etre modifie a cette etape.");
        }
        validateProjectText(titre, resume);

        project.setTitre(titre.trim());
        project.setResume(resume.trim());
        project.setKeywords(trimToNull(keywords));
        project.setStatut(ProjectStatus.SUBMITTED);
        applyAI(project);
        return project;
    }

    public AIAnalysisResult analyzeDraft(String titre, String resume) {
        return aiService.analyze(titre, resume);
    }

    public Project findProject(Long projectId) {
        Project project = requireProject(projectId);
        initializeProjectSummary(project);
        return project;
    }

    public Project findProjectWithDetails(Long projectId) {
        Project project = requireProject(projectId);
        initializeProjectDetails(project);
        return project;
    }

    public List<Project> findAllProjects() {
        List<Project> projects = entityManager.createNamedQuery("Project.findAllOrdered", Project.class)
                .getResultList();
        projects.forEach(this::initializeProjectSummary);
        return projects;
    }

    public List<Project> findProjectsByStudent(Long studentId) {
        List<Project> projects = entityManager.createNamedQuery("Project.findByStudent", Project.class)
                .setParameter("studentId", studentId)
                .getResultList();
        projects.forEach(this::initializeProjectSummary);
        return projects;
    }

    public List<Project> findProjectsBySupervisor(Long supervisorId) {
        List<Project> projects = entityManager.createNamedQuery("Project.findBySupervisor", Project.class)
                .setParameter("supervisorId", supervisorId)
                .getResultList();
        projects.forEach(this::initializeProjectSummary);
        return projects;
    }

    public List<Project> findVisibleProjects(User user) {
        if (user == null) {
            throw new SecurityException("Utilisateur non connecte.");
        }
        if (user.getRole() == Role.ADMIN) {
            return findAllProjects();
        }
        if (user.getRole() == Role.SUPERVISOR) {
            return findProjectsBySupervisor(user.getId());
        }
        return findProjectsByStudent(user.getId());
    }

    public SupervisorComment addSupervisorComment(Long projectId, Long supervisorId, String comment) {
        Project project = requireProject(projectId);
        User supervisor = requireUser(supervisorId, Role.SUPERVISOR);
        if (project.getSupervisor() == null || !project.getSupervisor().getId().equals(supervisor.getId())) {
            throw new SecurityException("Ce projet n'est pas affecte a ce superviseur.");
        }
        if (project.getStatut() == ProjectStatus.ARCHIVED) {
            throw new IllegalStateException("Un projet archive ne peut plus recevoir de commentaire.");
        }
        if (comment == null || comment.isBlank()) {
            throw new IllegalArgumentException("Le commentaire est obligatoire.");
        }

        SupervisorComment supervisorComment = new SupervisorComment(project, supervisor, comment.trim());
        entityManager.persist(supervisorComment);
        project.getSupervisorComments().add(supervisorComment);
        if (project.getStatut() == ProjectStatus.VALIDATED) {
            project.setStatut(ProjectStatus.IN_PROGRESS);
        }
        return supervisorComment;
    }

    public ProgressReport addProgressReport(Long projectId, Long authorId, String title, String content, Integer progressPercent) {
        Project project = requireProject(projectId);
        User author = requireActiveUser(authorId);
        if (!canReportProgress(project, author)) {
            throw new SecurityException("Vous ne pouvez pas ajouter un suivi sur ce projet.");
        }
        if (project.getStatut() == ProjectStatus.ARCHIVED) {
            throw new IllegalStateException("Un projet archive ne peut plus recevoir de suivi.");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Le titre du suivi est obligatoire.");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Le contenu du suivi est obligatoire.");
        }

        int safePercent = normalizePercent(progressPercent);
        ProgressReport report = new ProgressReport(project, author, title.trim(), content.trim(), safePercent);
        entityManager.persist(report);
        project.getProgressReports().add(report);
        if (project.getStatut() == ProjectStatus.VALIDATED) {
            project.setStatut(ProjectStatus.IN_PROGRESS);
        }
        return report;
    }

    public SupervisorComment findSupervisorComment(Long commentId, Long supervisorId) {
        SupervisorComment comment = requireComment(commentId);
        requireCommentAccess(comment, supervisorId);
        return comment;
    }

    public SupervisorComment updateSupervisorComment(Long commentId, Long supervisorId, String newComment) {
        SupervisorComment comment = requireComment(commentId);
        requireCommentAccess(comment, supervisorId);
        requireEditableContent(comment.getProject());
        if (newComment == null || newComment.isBlank()) {
            throw new IllegalArgumentException("Le commentaire est obligatoire.");
        }
        comment.setComment(newComment.trim());
        return comment;
    }

    public void deleteSupervisorComment(Long commentId, Long supervisorId) {
        SupervisorComment comment = requireComment(commentId);
        requireCommentAccess(comment, supervisorId);
        requireEditableContent(comment.getProject());
        comment.getProject().getSupervisorComments().remove(comment);
        entityManager.remove(comment);
    }

    public ProgressReport findProgressReport(Long reportId, Long userId) {
        ProgressReport report = requireReport(reportId);
        requireReportAccess(report, userId);
        return report;
    }

    public ProgressReport updateProgressReport(Long reportId, Long authorId, String title, String content, Integer progressPercent) {
        ProgressReport report = requireReport(reportId);
        requireReportAccess(report, authorId);
        requireEditableContent(report.getProject());
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Le titre du suivi est obligatoire.");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Le contenu du suivi est obligatoire.");
        }
        report.setTitle(title.trim());
        report.setContent(content.trim());
        report.setProgressPercent(normalizePercent(progressPercent));
        return report;
    }

    public void deleteProgressReport(Long reportId, Long userId) {
        ProgressReport report = requireReport(reportId);
        requireReportAccess(report, userId);
        requireEditableContent(report.getProject());
        report.getProject().getProgressReports().remove(report);
        entityManager.remove(report);
    }

    private void applyAI(Project project) {
        AIAnalysisResult result = aiService.analyze(project.getTitre(), project.getResume());
        project.setAiScore(result.getScore());
        project.setAiSuggestions(result.getSuggestionsText());
        project.setAiAnalyzedAt(LocalDateTime.now());
    }

    private Project requireProject(Long projectId) {
        Project project = projectId == null ? null : entityManager.find(Project.class, projectId);
        if (project == null) {
            throw new IllegalArgumentException("Projet introuvable.");
        }
        return project;
    }

    private User requireUser(Long userId, Role expectedRole) {
        User user = requireActiveUser(userId);
        if (user.getRole() != expectedRole) {
            throw new SecurityException("Role utilisateur incorrect.");
        }
        return user;
    }

    private User requireActiveUser(Long userId) {
        User user = userId == null ? null : entityManager.find(User.class, userId);
        if (user == null || !user.isActif()) {
            throw new IllegalArgumentException("Utilisateur introuvable ou inactif.");
        }
        return user;
    }

    private void validateProjectText(String titre, String resume) {
        if (titre == null || titre.isBlank()) {
            throw new IllegalArgumentException("Le titre est obligatoire.");
        }
        if (resume == null || resume.isBlank()) {
            throw new IllegalArgumentException("Le resume est obligatoire.");
        }
        if (resume.trim().length() < 40) {
            throw new IllegalArgumentException("Le resume doit contenir au moins 40 caracteres.");
        }
    }

    private boolean canReportProgress(Project project, User author) {
        if (author.getRole() == Role.ADMIN) {
            return true;
        }
        if (author.getRole() == Role.STUDENT) {
            return project.getStudent().getId().equals(author.getId());
        }
        return project.getSupervisor() != null && project.getSupervisor().getId().equals(author.getId());
    }

    private SupervisorComment requireComment(Long commentId) {
        SupervisorComment comment = commentId == null ? null : entityManager.find(SupervisorComment.class, commentId);
        if (comment == null) {
            throw new IllegalArgumentException("Commentaire introuvable.");
        }
        return comment;
    }

    private ProgressReport requireReport(Long reportId) {
        ProgressReport report = reportId == null ? null : entityManager.find(ProgressReport.class, reportId);
        if (report == null) {
            throw new IllegalArgumentException("Rapport de suivi introuvable.");
        }
        return report;
    }

    private void requireCommentAccess(SupervisorComment comment, Long userId) {
        User user = requireActiveUser(userId);
        boolean isAuthor = comment.getSupervisor() != null && comment.getSupervisor().getId().equals(user.getId());
        boolean isProjectSupervisor = comment.getProject().getSupervisor() != null
                && comment.getProject().getSupervisor().getId().equals(user.getId());
        if (user.getRole() != Role.ADMIN && !isAuthor && !isProjectSupervisor) {
            throw new SecurityException("Vous ne pouvez pas modifier ce commentaire.");
        }
    }

    private void requireReportAccess(ProgressReport report, Long userId) {
        User user = requireActiveUser(userId);
        boolean isAuthor = report.getAuthor().getId().equals(user.getId());
        boolean isProjectSupervisor = report.getProject().getSupervisor() != null
                && report.getProject().getSupervisor().getId().equals(user.getId());
        if (user.getRole() != Role.ADMIN && !isAuthor && !isProjectSupervisor) {
            throw new SecurityException("Vous ne pouvez pas modifier ce rapport de suivi.");
        }
    }

    private void requireEditableContent(Project project) {
        if (project.getStatut() == ProjectStatus.ARCHIVED) {
            throw new IllegalStateException("Un projet archive ne peut plus etre modifie.");
        }
    }

    private int normalizePercent(Integer progressPercent) {
        if (progressPercent == null) {
            return 0;
        }
        return Math.max(0, Math.min(100, progressPercent));
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private void initializeProjectSummary(Project project) {
        project.getStudent().getEmail();
        if (project.getSupervisor() != null) {
            project.getSupervisor().getEmail();
        }
        Evaluation evaluation = project.getEvaluation();
        if (evaluation != null) {
            evaluation.getFinalScore();
        }
    }

    private void initializeProjectDetails(Project project) {
        initializeProjectSummary(project);
        project.getSupervisorComments().size();
        project.getProgressReports().size();
    }
}
