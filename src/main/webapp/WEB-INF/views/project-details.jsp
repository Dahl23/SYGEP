<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${project.titre} - SYGEP</title>
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
                <c:if test="${sessionScope.currentUser.roleName == 'STUDENT'}">
                    <a href="${pageContext.request.contextPath}/proposal/new">Soumettre</a>
                </c:if>
                <c:if test="${sessionScope.currentUser.roleName == 'ADMIN'}">
                    <a href="${pageContext.request.contextPath}/admin/projects">Validation</a>
                </c:if>
                <c:if test="${sessionScope.currentUser.roleName == 'SUPERVISOR'}">
                    <a href="${pageContext.request.contextPath}/supervisor/dashboard">Supervision</a>
                </c:if>
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
            <div class="actions" style="justify-content:space-between;">
                <div>
                    <h1>${project.titre}</h1>
                    <p>Fiche complete du projet academique.</p>
                </div>
                <div class="actions">
                    <a class="button secondary" href="${pageContext.request.contextPath}/projects">Retour</a>
                    <c:if test="${sessionScope.currentUser.roleName == 'STUDENT' and project.editableByStudent}">
                        <a class="button" href="${pageContext.request.contextPath}/proposal/edit?id=${project.id}">Modifier</a>
                    </c:if>
                    <c:if test="${sessionScope.currentUser.roleName == 'SUPERVISOR' and project.statutName != 'ARCHIVED'}">
                        <a class="button" href="${pageContext.request.contextPath}/evaluation/new?projectId=${project.id}">Evaluer</a>
                    </c:if>
                </div>
            </div>
        </section>

        <section class="panel section">
            <div class="actions" style="justify-content:space-between;">
                <span class="badge ${project.statutName}" style="font-size:.9rem;padding:.4rem .9rem;">${project.statut.label}</span>
                <c:choose>
                    <c:when test="${not empty project.evaluation}">
                        <strong>Note finale : ${project.evaluation.finalScoreFormatted}/20</strong>
                    </c:when>
                    <c:otherwise><span class="muted">Pas encore evalue</span></c:otherwise>
                </c:choose>
            </div>

            <div class="detail-grid" style="margin-top:1.1rem;">
                <div class="detail-field">
                    <span class="label">Etudiant</span>
                    <div class="value">${project.student.displayName} <span class="muted">(${project.student.email})</span></div>
                </div>
                <div class="detail-field">
                    <span class="label">Superviseur</span>
                    <div class="value">
                        <c:choose>
                            <c:when test="${not empty project.supervisor}">${project.supervisor.displayName}</c:when>
                            <c:otherwise><span class="muted">Non affecte</span></c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <div class="detail-field"><span class="label">Soumis le</span><div class="value">${project.submittedAtText}</div></div>
                <div class="detail-field"><span class="label">Derniere mise a jour</span><div class="value">${project.updatedAtText}</div></div>
                <c:if test="${not empty project.validatedAt}">
                    <div class="detail-field"><span class="label">Valide le</span><div class="value">${project.validatedAtText}</div></div>
                </c:if>
                <c:if test="${not empty project.archivedAt}">
                    <div class="detail-field"><span class="label">Archive le</span><div class="value">${project.archivedAtText}</div></div>
                </c:if>
            </div>

            <c:if test="${not empty project.keywords}">
                <div class="chips" style="margin-top:1rem;">
                    <span class="chip">${project.keywords}</span>
                </div>
            </c:if>

            <div class="resume-box" style="margin-top:1rem;">${project.resume}</div>

            <div class="actions" style="margin-top:1rem;">
                <c:choose>
                    <c:when test="${project.hasDocument()}">
                        <a class="doc-link" style="font-size:1rem;padding:.6rem 1rem;" href="${pageContext.request.contextPath}/project/download?id=${project.id}">
                            <span class="doc-tag">${project.documentKind}</span> Telecharger le document (${project.documentFileName})
                        </a>
                        <span class="muted">${project.documentSizeText}</span>
                    </c:when>
                    <c:otherwise><span class="muted">Aucun document joint.</span></c:otherwise>
                </c:choose>
            </div>
        </section>

        <c:if test="${not empty project.evaluation}">
            <section class="panel section">
                <h2>Evaluation</h2>
                <div class="detail-grid">
                    <div class="detail-field"><span class="label">Technique (40%)</span><div class="value">${project.evaluation.technicalScoreFormatted}/20</div></div>
                    <div class="detail-field"><span class="label">Documentation (30%)</span><div class="value">${project.evaluation.documentationScoreFormatted}/20</div></div>
                    <div class="detail-field"><span class="label">Presentation (30%)</span><div class="value">${project.evaluation.presentationScoreFormatted}/20</div></div>
                    <div class="detail-field"><span class="label">Evalue le</span><div class="value">${project.evaluation.evaluatedAtText} par ${project.evaluation.evaluator.displayName}</div></div>
                </div>
                <div style="border-top:1px solid var(--border);margin-top:1rem;padding-top:.8rem;">
                    <span class="label">Note finale</span>
                    <div style="font-size:2rem;font-weight:800;color:var(--primary-dark);">${project.evaluation.finalScoreFormatted}/20</div>
                </div>
                <c:if test="${not empty project.evaluation.feedback}">
                    <div class="resume-box" style="margin-top:.8rem;">${project.evaluation.feedback}</div>
                </c:if>
            </section>
        </c:if>

        <section class="grid two">
            <article class="panel">
                <h2>Commentaires du superviseur</h2>
                <c:choose>
                    <c:when test="${empty project.supervisorComments}">
                        <p class="muted">Aucun commentaire pour le moment.</p>
                    </c:when>
                    <c:otherwise>
                        <div class="list">
                            <c:forEach var="comment" items="${project.supervisorComments}">
                                <div class="item">
                                    <p style="margin-top:0;">${comment.comment}</p>
                                    <p class="item-meta">${comment.supervisor.displayName} &middot; ${comment.createdAtText}</p>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </article>

            <article class="panel">
                <h2>Rapports de suivi</h2>
                <c:choose>
                    <c:when test="${empty project.progressReports}">
                        <p class="muted">Aucun rapport de suivi pour le moment.</p>
                    </c:when>
                    <c:otherwise>
                        <div class="list">
                            <c:forEach var="report" items="${project.progressReports}">
                                <div class="item">
                                    <h3>${report.title}</h3>
                                    <p style="margin:0;">${report.content}</p>
                                    <p class="item-meta">${report.author.displayName} &middot; ${report.createdAtText}</p>
                                    <div class="progress-label">Avancement : ${report.progressPercent}%</div>
                                    <div class="progress-track">
                                        <div class="progress-fill" style="width:${report.progressPercent}%;"></div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </article>
        </section>
    </main>
</div>
</body>
</html>