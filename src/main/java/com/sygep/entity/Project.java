package com.sygep.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "sygep_projects")
public class Project implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String titre;

    @Column(name = "resume", nullable = false, length = 4000)
    private String resume;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProjectStatus statut = ProjectStatus.BROUILLON;

    public Project() {
    }

    public Project(String titre, String resume, ProjectStatus statut) {
        this.titre = titre;
        this.resume = resume;
        this.statut = statut;
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

    public ProjectStatus getStatut() {
        return statut;
    }

    public void setStatut(ProjectStatus statut) {
        this.statut = statut;
    }
}
