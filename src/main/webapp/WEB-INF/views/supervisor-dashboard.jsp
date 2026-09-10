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
                        <nav class="pagination">
                            <c:if test="${projectsPage > 1}">
                                <a class="button secondary"
                                   href="${pageContext.request.contextPath}/supervisor/dashboard?page=${projectsPage - 1}">Precedent</a>
                            </c:if>
                            <span class="muted">Page ${projectsPage} / ${projectsTotalPages}</span>
                            <c:if test="${projectsPage < projectsTotalPages}">
                                <a class="button secondary"
                                   href="${pageContext.request.contextPath}/supervisor/dashboard?page=${projectsPage + 1}">Suivant</a>
                            </c:if>
                        </nav>
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
                        <nav class="pagination">
                            <c:if test="${readyPage > 1}">
                                <a class="button secondary"
                                   href="${pageContext.request.contextPath}/supervisor/dashboard?readyPage=${readyPage - 1}">Precedent</a>
                            </c:if>
                            <span class="muted">Page ${readyPage} / ${readyTotalPages}</span>
                            <c:if test="${readyPage < readyTotalPages}">
                                <a class="button secondary"
                                   href="${pageContext.request.contextPath}/supervisor/dashboard?readyPage=${readyPage + 1}">Suivant</a>
                            </c:if>
                        </nav>
                    </c:otherwise>
                </c:choose>
            </article>
        </section>

        <section class="panel section">
            <h2>Mes evaluations</h2>
            <c:choose>
                <c:when test="${empty evaluatedProjects}">
                    <p class="muted">Aucune evaluation effectuee pour le moment.</p>
                </c:when>
                <c:otherwise>
                    <div class="list">
                        <c:forEach var="project" items="${evaluatedProjects}">
                            <div class="item">
                                <h3>${project.titre}</h3>
                                <p><span class="badge ${project.statutName}">${project.statut.label}</span></p>
                                <p class="muted">${project.student.displayName}</p>
                                <p><strong>Note finale :</strong>
                                    <c:choose>
                                        <c:when test="${not empty project.evaluation.finalScore}">${project.evaluation.finalScore}/20</c:when>
                                        <c:otherwise>Non notee</c:otherwise>
                                    </c:choose>
                                </p>
                                <div class="actions">
                                    <a class="button secondary"
                                       href="${pageContext.request.contextPath}/evaluation/new?projectId=${project.id}">Re-evaluer</a>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                    <nav class="pagination">
                        <c:if test="${evaluatedPage > 1}">
                            <a class="button secondary"
                               href="${pageContext.request.contextPath}/supervisor/dashboard?evaluatedPage=${evaluatedPage - 1}">Precedent</a>
                        </c:if>
                        <span class="muted">Page ${evaluatedPage} / ${evaluatedTotalPages}</span>
                        <c:if test="${evaluatedPage < evaluatedTotalPages}">
                            <a class="button secondary"
                               href="${pageContext.request.contextPath}/supervisor/dashboard?evaluatedPage=${evaluatedPage + 1}">Suivant</a>
                        </c:if>
                    </nav>
                </c:otherwise>
            </c:choose>
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
                                <c:if test="${selectedProject.statutName != 'ARCHIVED'}">
                                    <div class="actions">
                                        <a class="button secondary"
                                           href="${pageContext.request.contextPath}/supervisor/dashboard?projectId=${selectedProject.id}&editCommentId=${comment.id}">Modifier</a>
                                        <form class="inline-form" method="post"
                                              action="${pageContext.request.contextPath}/supervisor/comment/delete">
                                            <input type="hidden" name="projectId" value="${selectedProject.id}">
                                            <input type="hidden" name="commentId" value="${comment.id}">
                                            <button type="submit" class="danger">Supprimer</button>
                                        </form>
                                    </div>
                                </c:if>
                            </div>
                        </c:forEach>
                    </div>

                    <c:if test="${selectedProject.statutName != 'ARCHIVED'}">
                        <c:choose>
                            <c:when test="${not empty editComment}">
                                <form class="stack" method="post"
                                      action="${pageContext.request.contextPath}/supervisor/comment/update">
                                    <input type="hidden" name="projectId" value="${selectedProject.id}">
                                    <input type="hidden" name="commentId" value="${editComment.id}">
                                    <label>Modifier le commentaire
                                        <textarea name="comment" required>${editComment.comment}</textarea>
                                    </label>
                                    <div class="actions">
                                        <button type="submit">Enregistrer</button>
                                        <a class="button secondary"
                                           href="${pageContext.request.contextPath}/supervisor/dashboard?projectId=${selectedProject.id}">Annuler</a>
                                    </div>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <form class="stack" method="post" action="${pageContext.request.contextPath}/supervisor/comment">
                                    <input type="hidden" name="projectId" value="${selectedProject.id}">
                                    <label>Nouveau commentaire
                                        <textarea name="comment" required></textarea>
                                    </label>
                                    <button type="submit">Ajouter</button>
                                </form>
                            </c:otherwise>
                        </c:choose>
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
                                <c:if test="${selectedProject.statutName != 'ARCHIVED'}">
                                    <div class="actions">
                                        <a class="button secondary"
                                           href="${pageContext.request.contextPath}/supervisor/dashboard?projectId=${selectedProject.id}&editReportId=${report.id}">Modifier</a>
                                        <form class="inline-form" method="post"
                                              action="${pageContext.request.contextPath}/supervisor/progress/delete">
                                            <input type="hidden" name="projectId" value="${selectedProject.id}">
                                            <input type="hidden" name="reportId" value="${report.id}">
                                            <button type="submit" class="danger">Supprimer</button>
                                        </form>
                                    </div>
                                </c:if>
                            </div>
                        </c:forEach>
                    </div>

                    <c:if test="${selectedProject.statutName != 'ARCHIVED'}">
                        <c:choose>
                            <c:when test="${not empty editReport}">
                                <form class="stack" method="post"
                                      action="${pageContext.request.contextPath}/supervisor/progress/update">
                                    <input type="hidden" name="projectId" value="${selectedProject.id}">
                                    <input type="hidden" name="reportId" value="${editReport.id}">
                                    <label>Titre
                                        <input name="title" value="${editReport.title}" required>
                                    </label>
                                    <label>Progression (%)
                                        <input name="progressPercent" type="number" min="0" max="100" value="${editReport.progressPercent}" required>
                                    </label>
                                    <label>Contenu
                                        <textarea name="content" required>${editReport.content}</textarea>
                                    </label>
                                    <div class="actions">
                                        <button type="submit">Enregistrer</button>
                                        <a class="button secondary"
                                           href="${pageContext.request.contextPath}/supervisor/dashboard?projectId=${selectedProject.id}">Annuler</a>
                                    </div>
                                </form>
                            </c:when>
                            <c:otherwise>
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
                            </c:otherwise>
                        </c:choose>
                    </c:if>
                </article>
            </section>
        </c:if>
    </main>
</div>
</body>
</html>