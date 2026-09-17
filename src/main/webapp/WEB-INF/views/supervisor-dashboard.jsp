<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Supervision - SYGEP</title>
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
                <a href="${pageContext.request.contextPath}/projects">Projets</a>
                <span class="user-chip">
                    <span class="user-name">${sessionScope.currentUser.displayName}</span>
                    <span class="role-tag ${sessionScope.currentUser.roleName}">${sessionScope.currentUser.roleName}</span>
                </span>
                <a href="${pageContext.request.contextPath}/logout">Deconnexion</a>
            </nav>
        </div>
    </header>

    <main class="container">
        <section class="page-head">
            <h1>Supervision</h1>
            <p>Commentaires, rapports de suivi et evaluations des projets affectes.</p>
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
                                    <p>
                                        <span class="badge ${project.statutName}">${project.statut.label}</span>
                                    </p>
                                    <p class="item-meta">${project.student.displayName} &middot; ${project.submittedAtText}</p>
                                    <div class="actions">
                                        <a class="button secondary small" href="${pageContext.request.contextPath}/supervisor/dashboard?projectId=${project.id}">Ouvrir</a>
                                        <a class="button secondary small" href="${pageContext.request.contextPath}/project/view?id=${project.id}">Fiche</a>
                                        <c:if test="${project.statutName != 'ARCHIVED'}">
                                            <a class="button small" href="${pageContext.request.contextPath}/evaluation/new?projectId=${project.id}">Evaluer</a>
                                        </c:if>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                        <nav class="pagination">
                            <c:if test="${projectsPage > 1}">
                                <a class="button secondary small" href="${pageContext.request.contextPath}/supervisor/dashboard?page=${projectsPage - 1}">Precedent</a>
                            </c:if>
                            <span class="current">Page ${projectsPage} / ${projectsTotalPages}</span>
                            <c:if test="${projectsPage < projectsTotalPages}">
                                <a class="button secondary small" href="${pageContext.request.contextPath}/supervisor/dashboard?page=${projectsPage + 1}">Suivant</a>
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
                                    <p class="item-meta">${project.student.displayName}</p>
                                    <div class="actions">
                                        <c:if test="${project.hasDocument()}">
                                            <a class="doc-link" href="${pageContext.request.contextPath}/project/download?id=${project.id}">
                                                <span class="doc-tag">${project.documentKind}</span> Document
                                            </a>
                                        </c:if>
                                        <span class="spacer"></span>
                                        <a class="button small" href="${pageContext.request.contextPath}/evaluation/new?projectId=${project.id}">Evaluer</a>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                        <nav class="pagination">
                            <c:if test="${readyPage > 1}">
                                <a class="button secondary small" href="${pageContext.request.contextPath}/supervisor/dashboard?readyPage=${readyPage - 1}">Precedent</a>
                            </c:if>
                            <span class="current">Page ${readyPage} / ${readyTotalPages}</span>
                            <c:if test="${readyPage < readyTotalPages}">
                                <a class="button secondary small" href="${pageContext.request.contextPath}/supervisor/dashboard?readyPage=${readyPage + 1}">Suivant</a>
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
                                <h3>${project.titre}
                                    <span class="badge ${project.statutName}" style="margin-left:.5rem;vertical-align:2px;">${project.statut.label}</span>
                                </h3>
                                <p class="item-meta">${project.student.displayName}</p>
                                <div class="actions">
                                    <c:if test="${not empty project.evaluation}">
                                        <p style="margin:0;"><strong>Note finale :</strong> ${project.evaluation.finalScoreFormatted}/20 &middot; ${project.evaluation.evaluatedAtText}</p>
                                    </c:if>
                                    <span class="spacer"></span>
                                    <a class="button secondary small" href="${pageContext.request.contextPath}/evaluation/new?projectId=${project.id}">Re-evaluer</a>
                                    <a class="button secondary small" href="${pageContext.request.contextPath}/project/view?id=${project.id}">Fiche</a>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                    <nav class="pagination">
                        <c:if test="${evaluatedPage > 1}">
                            <a class="button secondary small" href="${pageContext.request.contextPath}/supervisor/dashboard?evaluatedPage=${evaluatedPage - 1}">Precedent</a>
                        </c:if>
                        <span class="current">Page ${evaluatedPage} / ${evaluatedTotalPages}</span>
                        <c:if test="${evaluatedPage < evaluatedTotalPages}">
                            <a class="button secondary small" href="${pageContext.request.contextPath}/supervisor/dashboard?evaluatedPage=${evaluatedPage + 1}">Suivant</a>
                        </c:if>
                    </nav>
                </c:otherwise>
            </c:choose>
        </section>

        <c:if test="${not empty selectedProject}">
            <section class="section panel">
                <div class="actions" style="justify-content:space-between;">
                    <h2 style="margin:0;">${selectedProject.titre}</h2>
                    <span class="badge ${selectedProject.statutName}">${selectedProject.statut.label}</span>
                </div>
                <div class="detail-grid" style="margin-top:.8rem;">
                    <div class="detail-field"><span class="label">Etudiant</span><div class="value">${selectedProject.student.displayName}</div></div>
                    <div class="detail-field"><span class="label">Soumission</span><div class="value">${selectedProject.submittedAtText}</div></div>
                </div>
                <div class="resume-box" style="margin-top:.8rem;">${selectedProject.resume}</div>
                <div class="actions" style="margin-top:.8rem;">
                    <c:choose>
                        <c:when test="${selectedProject.hasDocument()}">
                            <a class="doc-link" href="${pageContext.request.contextPath}/project/download?id=${selectedProject.id}">
                                <span class="doc-tag">${selectedProject.documentKind}</span> Ouvrir le document
                            </a>
                        </c:when>
                        <c:otherwise><span class="muted">Aucun document joint</span></c:otherwise>
                    </c:choose>
                    <span class="spacer"></span>
                    <a class="button secondary small" href="${pageContext.request.contextPath}/project/view?id=${selectedProject.id}">Fiche complete</a>
                    <c:if test="${selectedProject.statutName != 'ARCHIVED'}">
                        <a class="button small" href="${pageContext.request.contextPath}/evaluation/new?projectId=${selectedProject.id}">Evaluer</a>
                    </c:if>
                </div>
            </section>

            <section class="grid two">
                <article class="panel">
                    <h2>Commentaires</h2>
                    <c:choose>
                        <c:when test="${empty selectedProject.supervisorComments}">
                            <p class="muted">Aucun commentaire pour le moment.</p>
                        </c:when>
                        <c:otherwise>
                            <div class="list">
                                <c:forEach var="comment" items="${selectedProject.supervisorComments}">
                                    <div class="item">
                                        <p style="margin-top:0;">${comment.comment}</p>
                                        <p class="item-meta">${comment.supervisor.displayName} &middot; ${comment.createdAtText}</p>
                                        <c:if test="${selectedProject.statutName != 'ARCHIVED'}">
                                            <div class="actions">
                                                <a class="button secondary small"
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
                        </c:otherwise>
                    </c:choose>

                    <c:if test="${selectedProject.statutName != 'ARCHIVED'}">
                        <div style="border-top:1px solid var(--border);margin-top:1rem;padding-top:1rem;">
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
                                            <a class="button secondary" href="${pageContext.request.contextPath}/supervisor/dashboard?projectId=${selectedProject.id}">Annuler</a>
                                        </div>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <form class="stack" method="post" action="${pageContext.request.contextPath}/supervisor/comment">
                                        <input type="hidden" name="projectId" value="${selectedProject.id}">
                                        <label>Ajouter un commentaire
                                            <textarea name="comment" placeholder="Votre retour au student sur le projet..." required></textarea>
                                        </label>
                                        <button type="submit">Ajouter</button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:if>
                </article>

                <article class="panel">
                    <h2>Rapports de suivi</h2>
                    <c:choose>
                        <c:when test="${empty selectedProject.progressReports}">
                            <p class="muted">Aucun rapport de suivi pour le moment.</p>
                        </c:when>
                        <c:otherwise>
                            <div class="list">
                                <c:forEach var="report" items="${selectedProject.progressReports}">
                                    <div class="item">
                                        <h3>${report.title}</h3>
                                        <p style="margin:0;">${report.content}</p>
                                        <p class="item-meta">${report.author.displayName} &middot; ${report.createdAtText}</p>
                                        <div class="progress-label">Avancement : ${report.progressPercent}%</div>
                                        <div class="progress-track">
                                            <div class="progress-fill" style="width:${report.progressPercent}%;"></div>
                                        </div>
                                        <c:if test="${selectedProject.statutName != 'ARCHIVED'}">
                                            <div class="actions" style="margin-top:.6rem;">
                                                <a class="button secondary small"
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
                        </c:otherwise>
                    </c:choose>

                    <c:if test="${selectedProject.statutName != 'ARCHIVED'}">
                        <div style="border-top:1px solid var(--border);margin-top:1rem;padding-top:1rem;">
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
                                            <a class="button secondary" href="${pageContext.request.contextPath}/supervisor/dashboard?projectId=${selectedProject.id}">Annuler</a>
                                        </div>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <form class="stack" method="post" action="${pageContext.request.contextPath}/supervisor/progress">
                                        <input type="hidden" name="projectId" value="${selectedProject.id}">
                                        <label>Titre
                                            <input name="title" placeholder="Rapport de suivi 1" required>
                                        </label>
                                        <label>Progression (%)
                                            <input name="progressPercent" type="number" min="0" max="100" value="50" required>
                                        </label>
                                        <label>Contenu
                                            <textarea name="content" placeholder="Avancement du travail, etapes realisees, difficultes..." required></textarea>
                                        </label>
                                        <button type="submit">Ajouter le suivi</button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:if>
                </article>
            </section>
        </c:if>
    </main>
</div>
</body>
</html>