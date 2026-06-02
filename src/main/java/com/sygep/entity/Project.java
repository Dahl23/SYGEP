package com.sygep.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "SygepProject")
@Table(name = "sygep_projects")
@NamedQueries({
        @NamedQuery(
                name = "Project.findAllOrdered",
                query = "SELECT p FROM SygepProject p ORDER BY p.createdAt DESC"
        ),
        @NamedQuery(
                name = "Project.findByStudent",
                query = "SELECT p FROM SygepProject p WHERE p.student.id = :studentId ORDER BY p.createdAt DESC"
        ),
        @NamedQuery(
                name = "Project.findBySupervisor",
                query = "SELECT p FROM SygepProject p WHERE p.supervisor.id = :supervisorId ORDER BY p.updatedAt DESC"
        ),
        @NamedQuery(
                name = "Project.findByStatus",
                query = "SELECT p FROM SygepProject p WHERE p.statut = :statut ORDER BY p.submittedAt ASC"
        )
})
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String titre;

    @Column(name = "resume", nullable = false, length = 6000)
    private String resume;

    @Column(name = "keywords", length = 500)
    private String keywords;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProjectStatus statut = ProjectStatus.SUBMITTED;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id")
    private User supervisor;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupervisorComment> supervisorComments = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgressReport> progressReports = new ArrayList<>();

    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Evaluation evaluation;

    @Column(name = "ai_score")
    private Integer aiScore;

    @Column(name = "ai_suggestions", length = 3000)
    private String aiSuggestions;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    @Column(name = "ai_analyzed_at")
    private LocalDateTime aiAnalyzedAt;

    public Project() {
    }

    public Project(String titre, String resume, String keywords, User student) {
        this.titre = titre;
        this.resume = resume;
        this.keywords = keywords;
        this.student = student;
        this.statut = ProjectStatus.SUBMITTED;
        this.submittedAt = LocalDateTime.now();
    }

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (submittedAt == null && statut == ProjectStatus.SUBMITTED) {
            submittedAt = now;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public ProjectStatus getStatut() {
        return statut;
    }

    public String getStatutName() {
        return statut == null ? "" : statut.name();
    }

    public void setStatut(ProjectStatus statut) {
        this.statut = statut;
        if (statut == ProjectStatus.SUBMITTED && submittedAt == null) {
            submittedAt = LocalDateTime.now();
        }
        if ((statut == ProjectStatus.VALIDATED || statut == ProjectStatus.IN_PROGRESS) && validatedAt == null) {
            validatedAt = LocalDateTime.now();
        }
        if (statut == ProjectStatus.ARCHIVED && archivedAt == null) {
            archivedAt = LocalDateTime.now();
        }
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public User getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(User supervisor) {
        this.supervisor = supervisor;
    }

    public List<SupervisorComment> getSupervisorComments() {
        return supervisorComments;
    }

    public void setSupervisorComments(List<SupervisorComment> supervisorComments) {
        this.supervisorComments = supervisorComments;
    }

    public List<ProgressReport> getProgressReports() {
        return progressReports;
    }

    public void setProgressReports(List<ProgressReport> progressReports) {
        this.progressReports = progressReports;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }

    public Integer getAiScore() {
        return aiScore;
    }

    public void setAiScore(Integer aiScore) {
        this.aiScore = aiScore;
    }

    public String getAiSuggestions() {
        return aiSuggestions;
    }

    public void setAiSuggestions(String aiSuggestions) {
        this.aiSuggestions = aiSuggestions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDateTime getValidatedAt() {
        return validatedAt;
    }

    public void setValidatedAt(LocalDateTime validatedAt) {
        this.validatedAt = validatedAt;
    }

    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(LocalDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }

    public LocalDateTime getAiAnalyzedAt() {
        return aiAnalyzedAt;
    }

    public void setAiAnalyzedAt(LocalDateTime aiAnalyzedAt) {
        this.aiAnalyzedAt = aiAnalyzedAt;
    }

    public boolean isEditableByStudent() {
        return statut == ProjectStatus.SUBMITTED || statut == ProjectStatus.REJECTED || statut == ProjectStatus.DRAFT;
    }
}
