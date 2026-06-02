package com.sygep.ejb;

import com.sygep.entity.Role;
import com.sygep.entity.User;
import com.sygep.util.PasswordUtil;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Locale;

@Stateless
public class AuthService {

    @PersistenceContext(unitName = "sygepPU")
    private EntityManager entityManager;

    public User authenticate(String email, String motDePasse) {
        if (email == null || email.isBlank() || motDePasse == null || motDePasse.isBlank()) {
            return null;
        }

        User user = findByEmail(email);
        if (user == null || !user.isActif()) {
            return null;
        }

        return PasswordUtil.hash(motDePasse).equals(user.getMotDePasse()) ? user : null;
    }

    public void ensureDemoUsers() {
        createUserIfMissing("Administrateur SYGEP", "admin@sygep.local", "admin123", Role.ADMIN);
        createUserIfMissing("Etudiant Demo", "student@sygep.local", "student123", Role.STUDENT);
        createUserIfMissing("Superviseur Demo", "supervisor@sygep.local", "supervisor123", Role.SUPERVISOR);
    }

    public User findUser(Long userId) {
        return userId == null ? null : entityManager.find(User.class, userId);
    }

    public User findByEmail(String email) {
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

    private User createUserIfMissing(String fullName, String email, String password, Role role) {
        User existing = findByEmail(email);
        if (existing != null) {
            return existing;
        }

        User user = new User(
                fullName,
                email.trim().toLowerCase(Locale.ROOT),
                PasswordUtil.hash(password),
                role,
                true
        );
        entityManager.persist(user);
        return user;
    }
}
