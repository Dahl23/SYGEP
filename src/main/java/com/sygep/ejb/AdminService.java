package com.sygep.ejb;

import com.sygep.entity.User;
import com.sygep.entity.UserRole;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;

@Stateless
public class AdminService {

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

        return hashPassword(motDePasse).equals(user.getMotDePasse()) ? user : null;
    }

    public User createUser(String email, String motDePasse, UserRole role) {
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
                email.trim().toLowerCase(Locale.ROOT),
                hashPassword(motDePasse),
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

    private User findByEmail(String email) {
        List<User> results = entityManager.createQuery(
                        "SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)", User.class)
                .setParameter("email", email.trim())
                .setMaxResults(1)
                .getResultList();

        return results.isEmpty() ? null : results.get(0);
    }

    private String hashPassword(String motDePasse) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(motDePasse.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte value : hash) {
                builder.append(String.format("%02x", value));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 indisponible", exception);
        }
    }
}
