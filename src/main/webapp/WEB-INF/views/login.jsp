<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Connexion - SYGEP</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sygep.css">
</head>
<body>
<main class="login-shell">
    <section class="login-panel">
        <div class="login-brand">
            <span class="brand-dot"></span>
            <span style="font-weight:800;font-size:1.3rem;">SYGEP</span>
        </div>
        <p class="login-title">Connexion</p>
        <p class="login-sub">Systeme de Gestion et d'Evaluation des Projets Academiques</p>

        <c:if test="${not empty error}">
            <div class="message error">${error}</div>
        </c:if>

        <form class="stack" method="post" action="${pageContext.request.contextPath}/login">
            <label>Adresse email
                <input name="email" type="email" value="${email}" placeholder="vous@exemple.com" required autofocus>
            </label>
            <label>Mot de passe
                <input name="motDePasse" type="password" placeholder="&#8226;&#8226;&#8226;&#8226;&#8226;&#8226;&#8226;&#8226;" required>
            </label>
            <button type="submit">Se connecter</button>
        </form>

        <div class="demo-box">
            <strong>Comptes de demonstration</strong>
            <ul>
                <li>admin@sygep.local / admin123</li>
                <li>student@sygep.local / student123</li>
                <li>supervisor@sygep.local / supervisor123</li>
            </ul>
        </div>
    </section>
</main>
</body>
</html>