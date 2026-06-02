<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Connexion - SYGEP</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sygep.css">
</head>
<body>
<main class="login-shell">
    <section class="login-panel">
        <h1>SYGEP</h1>
        <p class="muted">Systeme de Gestion et d'Evaluation des Projets Academiques avec IA</p>

        <c:if test="${not empty error}">
            <div class="message error">${error}</div>
        </c:if>

        <form class="stack" method="post" action="${pageContext.request.contextPath}/login">
            <label>Email
                <input name="email" type="email" value="${email}" required>
            </label>
            <label>Mot de passe
                <input name="motDePasse" type="password" required>
            </label>
            <button type="submit">Se connecter</button>
        </form>

        <p class="muted">
            Comptes de demonstration : admin@sygep.local/admin123,
            student@sygep.local/student123,
            supervisor@sygep.local/supervisor123.
        </p>
    </section>
</main>
</body>
</html>
