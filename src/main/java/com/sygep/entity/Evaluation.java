package com.sygep.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity(name = "SygepEvaluation")
@Table(name = "sygep_evaluations")
public class Evaluation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false, unique = true)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evaluator_id", nullable = false)
    private User evaluator;

    @Column(name = "technical_score", nullable = false, precision = 4, scale = 2)
    private BigDecimal technicalScore = BigDecimal.ZERO;

    @Column(name = "documentation_score", nullable = false, precision = 4, scale = 2)
    private BigDecimal documentationScore = BigDecimal.ZERO;

    @Column(name = "presentation_score", nullable = false, precision = 4, scale = 2)
    private BigDecimal presentationScore = BigDecimal.ZERO;

    @Column(name = "final_score", nullable = false, precision = 4, scale = 2)
    private BigDecimal finalScore = BigDecimal.ZERO;

    @Column(length = 3000)
    private String feedback;

    @Column(name = "evaluated_at", nullable = false)
    private LocalDateTime evaluatedAt;

    public Evaluation() {
    }

    @PrePersist
    public void onCreate() {
        evaluatedAt = LocalDateTime.now();
        calculateFinalScore();
    }

    @PreUpdate
    public void onUpdate() {
        calculateFinalScore();
    }

    public void calculateFinalScore() {
        BigDecimal technical = technicalScore == null ? BigDecimal.ZERO : technicalScore;
        BigDecimal documentation = documentationScore == null ? BigDecimal.ZERO : documentationScore;
        BigDecimal presentation = presentationScore == null ? BigDecimal.ZERO : presentationScore;

        finalScore = technical.multiply(new BigDecimal("0.40"))
                .add(documentation.multiply(new BigDecimal("0.30")))
                .add(presentation.multiply(new BigDecimal("0.30")))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public Long getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(User evaluator) {
        this.evaluator = evaluator;
    }

    public BigDecimal getTechnicalScore() {
        return technicalScore;
    }

    public void setTechnicalScore(BigDecimal technicalScore) {
        this.technicalScore = technicalScore;
    }

    public BigDecimal getDocumentationScore() {
        return documentationScore;
    }

    public void setDocumentationScore(BigDecimal documentationScore) {
        this.documentationScore = documentationScore;
    }

    public BigDecimal getPresentationScore() {
        return presentationScore;
    }

    public void setPresentationScore(BigDecimal presentationScore) {
        this.presentationScore = presentationScore;
    }

    public BigDecimal getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(BigDecimal finalScore) {
        this.finalScore = finalScore;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public LocalDateTime getEvaluatedAt() {
        return evaluatedAt;
    }

    public void setEvaluatedAt(LocalDateTime evaluatedAt) {
        this.evaluatedAt = evaluatedAt;
    }
}
