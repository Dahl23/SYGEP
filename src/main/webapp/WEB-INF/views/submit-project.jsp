<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Soumission de projet - SYGEP</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sygep.css">
</head>
<body>
<div class="page">
    <header class="topbar">
        <div class="topbar-inner">
            <a class="brand" href="${pageContext.request.contextPath}/dashboard">SYGEP</a>
            <nav class="nav">
                <a href="${pageContext.request.contextPath}/dashboard">Tableau de bord</a>
                <a href="${pageContext.request.contextPath}/projects">Mes projets</a>
                <a href="${pageContext.request.contextPath}/logout">Deconnexion</a>
            </nav>
        </div>
    </header>

    <main class="container">
        <c:set var="editing" value="${not empty project or not empty projectId}" />
        <c:set var="titreValue" value="${not empty formTitre ? formTitre : project.titre}" />
        <c:set var="resumeValue" value="${not empty formResume ? formResume : project.resume}" />
        <c:set var="keywordsValue" value="${not empty formKeywords ? formKeywords : project.keywords}" />
        <c:set var="effectiveProjectId" value="${not empty project ? project.id : projectId}" />
        <c:set var="submitAction" value="${pageContext.request.contextPath}/proposal/submit" />
        <c:if test="${editing}">
            <c:set var="submitAction" value="${pageContext.request.contextPath}/proposal/update" />
        </c:if>

        <section class="section">
            <h1><c:choose><c:when test="${editing}">Modifier le projet</c:when><c:otherwise>Soumettre un projet</c:otherwise></c:choose></h1>
            <p class="muted">Le projet est soumis avec une analyse IA simple de la qualite du resume.</p>
        </section>

        <c:if test="${not empty error}">
            <div class="message error">${error}</div>
        </c:if>
        <c:if test="${not empty success}">
            <div class="message success">${success}</div>
        </c:if>

        <section class="grid two">
            <form class="panel stack" method="post" action="${submitAction}">
                <c:if test="${editing}">
                    <input type="hidden" name="projectId" value="${effectiveProjectId}">
                </c:if>

                <label>Titre
                    <input name="titre" maxlength="255" value="${titreValue}" required>
                </label>

                <label>Mots-cles
                    <input name="keywords" maxlength="500" value="${keywordsValue}" placeholder="Jakarta EE, IA, PostgreSQL">
                </label>

                <label>Resume
                    <textarea name="resume" required>${resumeValue}</textarea>
                </label>

                <div class="actions">
                    <button type="submit" formaction="${pageContext.request.contextPath}/proposal/analyze" class="secondary">Analyser IA</button>
                    <button type="submit">
                        <c:choose><c:when test="${editing}">Mettre a jour</c:when><c:otherwise>Soumettre</c:otherwise></c:choose>
                    </button>
                </div>
            </form>

            <aside class="panel">
                <h2>Analyse IA</h2>
                <c:choose>
                    <c:when test="${not empty analysis}">
                        <div class="ai-box">
                            <p><strong>Score :</strong> ${analysis.score}/100 (${analysis.qualityLabel})</p>
                            <p><strong>Longueur :</strong> ${analysis.wordCount} mots</p>
                            <ul>
                                <c:forEach var="suggestion" items="${analysis.suggestions}">
                                    <li>${suggestion}</li>
                                </c:forEach>
                            </ul>
                        </div>
                    </c:when>
                    <c:when test="${not empty project.aiScore}">
                        <div class="ai-box">
                            <p><strong>Dernier score :</strong> ${project.aiScore}/100</p>
                            <pre>${project.aiSuggestions}</pre>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p class="muted">Utilise le bouton d'analyse avant la soumission pour obtenir un retour rapide.</p>
                    </c:otherwise>
                </c:choose>
            </aside>
        </section>
    </main>
</div>
</body>
</html>
