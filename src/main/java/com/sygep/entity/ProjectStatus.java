package com.sygep.entity;

public enum ProjectStatus {
    DRAFT("Brouillon"),
    SUBMITTED("Soumis"),
    VALIDATED("Valide"),
    REJECTED("Rejete"),
    IN_PROGRESS("En suivi"),
    EVALUATED("Evalue"),
    ARCHIVED("Archive");

    private final String label;

    ProjectStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
