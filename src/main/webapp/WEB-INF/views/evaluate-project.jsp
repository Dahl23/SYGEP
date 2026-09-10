<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Evaluation - SYGEP</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sygep.css">
</head>
<body>
<div class="page">
    <header class="topbar">
        <div class="topbar-inner">
            <a class="brand" href="${pageContext.request.contextPath}/dashboard">SYGEP</a>
            <nav class="nav">
                <a href="${pageContext.request.contextPath}/supervisor/dashboard">Supervision</a>
                <a href="${pageContext.request.contextPath}/projects">Projets</a>
                <a href="${pageContext.request.contextPath}/logout">Deconnexion</a>
            </nav>
        </div>
    </header>

    <main class="container">
        <section class="section">
            <h1>Evaluation</h1>
            <p class="muted">La note finale est calculee automatiquement : technique 40 %, documentation 30 %, presentation 30 %.</p>
        </section>

        <c:if test="${not empty evaluation}">
            <div class="message success">Projet deja evalue (note actuelle : ${evaluation.finalScore}/20). Cette page met a jour l'evaluation existante.</div>
        </c:if>

        <c:if test="${not empty error}">
            <div class="message error">${error}</div>
        </c:if>

        <section class="grid two">
            <article class="panel">
                <h2>${project.titre}</h2>
                <p><span class="badge ${project.statutName}">${project.statut.label}</span></p>
                <p>${project.resume}</p>
                <p><strong>Etudiant :</strong> ${project.student.displayName}</p>
                <p><strong>Score IA :</strong>
                    <c:choose>
                        <c:when test="${not empty project.aiScore}">${project.aiScore}/100</c:when>
                        <c:otherwise>Non analyse</c:otherwise>
                    </c:choose>
                </p>
                <c:if test="${not empty project.aiSuggestions}">
                    <pre>${project.aiSuggestions}</pre>
                </c:if>
            </article>

            <form class="panel stack" method="post" action="${pageContext.request.contextPath}/evaluation/save">
                <input type="hidden" name="projectId" value="${project.id}">

                <label>Note technique /20
                    <input name="technicalScore" type="number" min="0" max="20" step="0.25" value="${evaluation.technicalScore}" required>
                </label>

                <label>Note documentation /20
                    <input name="documentationScore" type="number" min="0" max="20" step="0.25" value="${evaluation.documentationScore}" required>
                </label>

                <label>Note presentation /20
                    <input name="presentationScore" type="number" min="0" max="20" step="0.25" value="${evaluation.presentationScore}" required>
                </label>

                <label>Feedback
                    <textarea name="feedback">${evaluation.feedback}</textarea>
                </label>

                <c:choose>
                    <c:when test="${not empty evaluation}">
                        <button type="submit">Mettre a jour l'evaluation</button>
                    </c:when>
                    <c:otherwise>
                        <button type="submit">Enregistrer et archiver</button>
                    </c:otherwise>
                </c:choose>
            </form>
        </section>
    </main>
</div>
</body>
</html>
