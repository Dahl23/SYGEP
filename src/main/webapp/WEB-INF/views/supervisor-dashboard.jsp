<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Supervision - SYGEP</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sygep.css">
</head>
<body>
<div class="page">
    <header class="topbar">
        <div class="topbar-inner">
            <a class="brand" href="${pageContext.request.contextPath}/dashboard">SYGEP</a>
            <nav class="nav">
                <a href="${pageContext.request.contextPath}/dashboard">Tableau de bord</a>
                <a href="${pageContext.request.contextPath}/projects">Projets</a>
                <a href="${pageContext.request.contextPath}/logout">Deconnexion</a>
            </nav>
        </div>
    </header>

    <main class="container">
        <section class="section">
            <h1>Supervision</h1>
            <p class="muted">Commentaires, rapports de suivi et evaluations des projets affectes.</p>
        </section>

        <c:if test="${not empty success}">
            <div class="message success">${success}</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="message error">${error}</div>
        </c:if>

        <section class="grid two">
            <article class="panel">
                <h2>Projets affectes</h2>
                <c:choose>
                    <c:when test="${empty projects}">
                        <p class="muted">Aucun projet affecte.</p>
                    </c:when>
                    <c:otherwise>
                        <div class="list">
                            <c:forEach var="project" items="${projects}">
                                <div class="item">
                                    <h3>${project.titre}</h3>
                                    <p><span class="badge ${project.statutName}">${project.statut.label}</span></p>
                                    <p class="muted">${project.student.displayName}</p>
                                    <div class="actions">
                                        <a class="button secondary" href="${pageContext.request.contextPath}/supervisor/dashboard?projectId=${project.id}">Ouvrir</a>
                                        <c:if test="${project.statutName != 'ARCHIVED'}">
                                            <a class="button" href="${pageContext.request.contextPath}/evaluation/new?projectId=${project.id}">Evaluer</a>
                                        </c:if>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </article>

            <article class="panel">
                <h2>Prets pour evaluation</h2>
                <c:choose>
                    <c:when test="${empty readyProjects}">
                        <p class="muted">Aucun projet pret pour le moment.</p>
                    </c:when>
                    <c:otherwise>
                        <div class="list">
                            <c:forEach var="project" items="${readyProjects}">
                                <div class="item">
                                    <strong>${project.titre}</strong>
                                    <p class="muted">${project.student.displayName}</p>
                                    <a class="button" href="${pageContext.request.contextPath}/evaluation/new?projectId=${project.id}">Evaluer</a>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </article>
        </section>

        <c:if test="${not empty selectedProject}">
            <section class="section panel">
                <h2>${selectedProject.titre}</h2>
                <p><span class="badge ${selectedProject.statutName}">${selectedProject.statut.label}</span></p>
                <p>${selectedProject.resume}</p>
                <p><strong>Etudiant :</strong> ${selectedProject.student.displayName}</p>
                <p><strong>Score IA :</strong>
                    <c:choose>
                        <c:when test="${not empty selectedProject.aiScore}">${selectedProject.aiScore}/100</c:when>
                        <c:otherwise>Non analyse</c:otherwise>
                    </c:choose>
                </p>
            </section>

            <section class="grid two">
                <article class="panel">
                    <h2>Commentaires</h2>
                    <div class="list">
                        <c:forEach var="comment" items="${selectedProject.supervisorComments}">
                            <div class="item">
                                <p>${comment.comment}</p>
                                <p class="muted">${comment.createdAt}</p>
                            </div>
                        </c:forEach>
                    </div>

                    <c:if test="${selectedProject.statutName != 'ARCHIVED'}">
                        <form class="stack" method="post" action="${pageContext.request.contextPath}/supervisor/comment">
                            <input type="hidden" name="projectId" value="${selectedProject.id}">
                            <label>Nouveau commentaire
                                <textarea name="comment" required></textarea>
                            </label>
                            <button type="submit">Ajouter</button>
                        </form>
                    </c:if>
                </article>

                <article class="panel">
                    <h2>Rapports de suivi</h2>
                    <div class="list">
                        <c:forEach var="report" items="${selectedProject.progressReports}">
                            <div class="item">
                                <h3>${report.title}</h3>
                                <p>${report.content}</p>
                                <p class="muted">${report.progressPercent}% - ${report.createdAt}</p>
                            </div>
                        </c:forEach>
                    </div>

                    <c:if test="${selectedProject.statutName != 'ARCHIVED'}">
                        <form class="stack" method="post" action="${pageContext.request.contextPath}/supervisor/progress">
                            <input type="hidden" name="projectId" value="${selectedProject.id}">
                            <label>Titre
                                <input name="title" required>
                            </label>
                            <label>Progression (%)
                                <input name="progressPercent" type="number" min="0" max="100" value="50" required>
                            </label>
                            <label>Contenu
                                <textarea name="content" required></textarea>
                            </label>
                            <button type="submit">Ajouter le suivi</button>
                        </form>
                    </c:if>
                </article>
            </section>
        </c:if>
    </main>
</div>
</body>
</html>
