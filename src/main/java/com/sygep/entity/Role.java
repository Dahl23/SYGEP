package com.sygep.entity;

public enum Role {
    ADMIN("Administrateur"),
    STUDENT("Etudiant"),
    SUPERVISOR("Superviseur");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
