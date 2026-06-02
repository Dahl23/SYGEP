package com.sygep.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "SygepUser")
@Table(name = "sygep_users")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", length = 150)
    private String fullName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "mot_de_passe", nullable = false, length = 255)
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false)
    private boolean actif = true;

    @OneToMany(mappedBy = "student")
    private List<Project> submittedProjects = new ArrayList<>();

    @OneToMany(mappedBy = "supervisor")
    private List<Project> supervisedProjects = new ArrayList<>();

    @OneToMany(mappedBy = "supervisor")
    private List<SupervisorComment> supervisorComments = new ArrayList<>();

    @OneToMany(mappedBy = "author")
    private List<ProgressReport> progressReports = new ArrayList<>();

    @OneToMany(mappedBy = "evaluator")
    private List<Evaluation> evaluations = new ArrayList<>();

    public User() {
    }

    public User(String fullName, String email, String motDePasse, Role role, boolean actif) {
        this.fullName = fullName;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
        this.actif = actif;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDisplayName() {
        return fullName == null || fullName.isBlank() ? email : fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public Role getRole() {
        return role;
    }

    public String getRoleName() {
        return role == null ? "" : role.name();
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public List<Project> getSubmittedProjects() {
        return submittedProjects;
    }

    public void setSubmittedProjects(List<Project> submittedProjects) {
        this.submittedProjects = submittedProjects;
    }

    public List<Project> getSupervisedProjects() {
        return supervisedProjects;
    }

    public void setSupervisedProjects(List<Project> supervisedProjects) {
        this.supervisedProjects = supervisedProjects;
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

    public List<Evaluation> getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(List<Evaluation> evaluations) {
        this.evaluations = evaluations;
    }
}
