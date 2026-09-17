<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Soumission de projet - SYGEP</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sygep.css">
</head>
<body>
<div class="page">
    <header class="topbar">
        <div class="topbar-inner">
            <a class="brand" href="${pageContext.request.contextPath}/dashboard">
                <span class="brand-dot"></span>SYGEP
            </a>
            <nav class="nav">
                <a href="${pageContext.request.contextPath}/dashboard">Tableau de bord</a>
                <a href="${pageContext.request.contextPath}/projects">Mes projets</a>
                <span class="user-chip">
                    <span class="user-name">${sessionScope.currentUser.displayName}</span>
                    <span class="role-tag ${sessionScope.currentUser.roleName}">${sessionScope.currentUser.roleName}</span>
                </span>
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

        <section class="page-head">
            <h1><c:choose><c:when test="${editing}">Modifier le projet</c:when><c:otherwise>Soumettre un projet</c:otherwise></c:choose></h1>
            <p>Fournissez les informations du projet ainsi qu'un document PDF ou Word.</p>
        </section>

        <c:if test="${not empty error}">
            <div class="message error">${error}</div>
        </c:if>

        <section class="panel" style="max-width:760px;">
            <form class="stack" method="post" action="${submitAction}" enctype="multipart/form-data">
                <c:if test="${editing}">
                    <input type="hidden" name="projectId" value="${effectiveProjectId}">
                </c:if>

                <label>Titre du projet
                    <input name="titre" maxlength="255" value="${titreValue}" placeholder="Ex : Plateforme de gestion des bibliotheques universitaires" required>
                </label>

                <label>Mots-cles
                    <input name="keywords" maxlength="500" value="${keywordsValue}" placeholder="Jakarta EE, PostgreSQL, Gestion de projet">
                    <span class="hint">Separes par des virgules.</span>
                </label>

                <label>Resume
                    <textarea name="resume" placeholder="Decrivez le contexte, la problematique, les objectifs et la methodologie." required>${resumeValue}</textarea>
                    <span class="hint">Minimum 40 caracteres.</span>
                </label>

                <c:choose>
                    <c:when test="${editing and project.hasDocument()}">
                        <div class="file-card">
                            <span class="file-icon">${project.documentKind}</span>
                            <div>
                                <div class="file-name">${project.documentFileName}</div>
                                <div class="file-meta">${project.documentSizeText} - document actuel</div>
                            </div>
                            <span class="spacer"></span>
                            <a class="doc-link" href="${pageContext.request.contextPath}/project/download?id=${project.id}">Ouvrir</a>
                        </div>
                        <label>Remplacer le document (optionnel)
                            <input type="file" name="document" accept=".pdf,.doc,.docx,application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document">
                            <span class="hint">PDF ou Word, 10 Mo maximum. Laissez vide pour conserver le document actuel.</span>
                        </label>
                    </c:when>
                    <c:otherwise>
                        <label>Document du projet
                            <input type="file" name="document" accept=".pdf,.doc,.docx,application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document" required>
                            <span class="hint">PDF ou Word, 10 Mo maximum.</span>
                        </label>
                    </c:otherwise>
                </c:choose>

                <div class="actions">
                    <a class="button secondary" href="${pageContext.request.contextPath}/projects">Annuler</a>
                    <span class="spacer"></span>
                    <button type="submit">
                        <c:choose><c:when test="${editing}">Mettre a jour</c:when><c:otherwise>Soumettre</c:otherwise></c:choose>
                    </button>
                </div>
            </form>
        </section>
    </main>
</div>
</body>
</html>