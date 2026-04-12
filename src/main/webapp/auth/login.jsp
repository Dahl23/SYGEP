<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Connexion SYGEP</title>
    <style>
        body {
            font-family: "Segoe UI", sans-serif;
            background: #f5f7fa;
            margin: 0;
            padding: 3rem 1rem;
        }

        .card {
            max-width: 420px;
            margin: 0 auto;
            background: #ffffff;
            border-radius: 12px;
            padding: 2rem;
            box-shadow: 0 12px 30px rgba(15, 23, 42, 0.08);
        }

        h1 {
            margin-top: 0;
            color: #0f172a;
        }

        label {
            display: block;
            font-weight: 600;
            margin-top: 1rem;
            color: #334155;
        }

        input {
            width: 100%;
            box-sizing: border-box;
            padding: 0.8rem;
            margin-top: 0.35rem;
            border: 1px solid #cbd5e1;
            border-radius: 8px;
        }

        button {
            width: 100%;
            margin-top: 1.2rem;
            padding: 0.85rem;
            border: 0;
            border-radius: 8px;
            background: #0f766e;
            color: #ffffff;
            font-weight: 700;
            cursor: pointer;
        }

        .error {
            margin-top: 1rem;
            padding: 0.85rem;
            border-radius: 8px;
            background: #fee2e2;
            color: #991b1b;
        }
    </style>
</head>
<body>
<main class="card">
    <h1>SYGEP</h1>
    <p>Connexion a la plateforme de gestion et d'evaluation des projets academiques.</p>

    <c:if test="${not empty error}">
        <div class="error">${error}</div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/login">
        <label for="email">Email</label>
        <input id="email" name="email" type="email" placeholder="nom@universite.bi" required>

        <label for="motDePasse">Mot de passe</label>
        <input id="motDePasse" name="motDePasse" type="password" required>

        <button type="submit">Se connecter</button>
    </form>
</main>
</body>
</html>
