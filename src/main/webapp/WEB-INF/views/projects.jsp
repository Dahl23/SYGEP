<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Projets - SYGEP</title>
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
            <h1>Projets</h1>
            <p>Vue adaptee au role connecte : mes projets, projets affectes ou tous les projets.</p>
        </section>

        <c:if test="${not empty success}">
            <div class="message success">${success}</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="message error">${error}</div>
        </c:if>

        <section class="panel">
            <c:choose>
                <c:when test="${empty projects}">
                    <p class="muted">Aucun projet a afficher.</p>
                </c:when>
                <c:otherwise>
                    <div class="table-wrap">
                        <table>
                            <thead>
                            <tr>
                                <th>Titre</th>
                                <th>Statut</th>
                                <th>Etudiant</th>
                                <th>Superviseur</th>
                                <th>Document</th>
                                <th>Note finale</th>
                                <th>Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="project" items="${projects}">
                                <tr>
                                    <td>
                                        <span class="cell-main">${project.titre}</span>
                                        <c:if test="${not empty project.keywords}">
                                            <div>
                                                <span class="chips">
                                                    <span class="chip">${project.keywords}</span>
                                                </span>
                                            </div>
                                        </c:if>
                                    </td>
                                    <td><span class="badge ${project.statutName}">${project.statut.label}</span></td>
                                    <td>${project.student.displayName}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty project.supervisor}">${project.supervisor.displayName}</c:when>
                                            <c:otherwise><span class="muted">Non affecte</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${project.hasDocument()}">
                                                <a class="doc-link" href="${pageContext.request.contextPath}/project/download?id=${project.id}">
                                                    <span class="doc-tag">${project.documentKind}</span> Ouvrir
                                                </a>
                                            </c:when>
                                            <c:otherwise><span class="muted">Aucun</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty project.evaluation}">
                                                <strong>${project.evaluation.finalScoreFormatted}/20</strong>
                                            </c:when>
                                            <c:otherwise><span class="muted">-</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div class="actions">
                                            <a class="button secondary small" href="${pageContext.request.contextPath}/project/view?id=${project.id}">Voir</a>
                                            <c:if test="${sessionScope.currentUser.roleName == 'STUDENT' and project.editableByStudent}">
                                                <a class="button small" href="${pageContext.request.contextPath}/proposal/edit?id=${project.id}">Modifier</a>
                                            </c:if>
                                            <c:if test="${sessionScope.currentUser.roleName == 'SUPERVISOR'}">
                                                <a class="button secondary small" href="${pageContext.request.contextPath}/supervisor/dashboard?projectId=${project.id}">Suivre</a>
                                            </c:if>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
    </main>
</div>
</body>
</html>