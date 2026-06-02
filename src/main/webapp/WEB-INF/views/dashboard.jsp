<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Tableau de bord - SYGEP</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sygep.css">
</head>
<body>
<div class="page">
    <header class="topbar">
        <div class="topbar-inner">
            <a class="brand" href="${pageContext.request.contextPath}/dashboard">SYGEP</a>
            <nav class="nav">
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
                <a href="${pageContext.request.contextPath}/logout">Deconnexion</a>
            </nav>
        </div>
    </header>

    <main class="container">
        <c:if test="${not empty success}">
            <div class="message success">${success}</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="message error">${error}</div>
        </c:if>

        <section class="section">
            <h1>Tableau de bord</h1>
            <p class="muted">Connecte : ${sessionScope.currentUser.displayName} (${sessionScope.currentUser.role.label})</p>
        </section>

        <section class="section grid three">
            <c:forEach var="status" items="${statuses}">
                <article class="stat">
                    <strong>${statusCounts[status]}</strong>
                    <span>${status.label}</span>
                </article>
            </c:forEach>
        </section>

        <section class="section panel">
            <h2>Actions rapides</h2>
            <div class="actions">
                <a class="button secondary" href="${pageContext.request.contextPath}/projects">Consulter les projets</a>
                <c:if test="${sessionScope.currentUser.roleName == 'STUDENT'}">
                    <a class="button" href="${pageContext.request.contextPath}/proposal/new">Soumettre un projet</a>
                </c:if>
                <c:if test="${sessionScope.currentUser.roleName == 'ADMIN'}">
                    <a class="button" href="${pageContext.request.contextPath}/admin/projects">Valider les projets</a>
                </c:if>
                <c:if test="${sessionScope.currentUser.roleName == 'SUPERVISOR'}">
                    <a class="button" href="${pageContext.request.contextPath}/supervisor/dashboard">Ouvrir la supervision</a>
                </c:if>
            </div>
        </section>

        <section class="section panel">
            <h2>Projets recents</h2>
            <c:choose>
                <c:when test="${empty projects}">
                    <p class="muted">Aucun projet disponible pour le moment.</p>
                </c:when>
                <c:otherwise>
                    <table>
                        <thead>
                        <tr>
                            <th>Titre</th>
                            <th>Statut</th>
                            <th>Etudiant</th>
                            <th>Superviseur</th>
                            <th>IA</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="project" items="${projects}">
                            <tr>
                                <td>${project.titre}</td>
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
                                        <c:when test="${not empty project.aiScore}">${project.aiScore}/100</c:when>
                                        <c:otherwise><span class="muted">-</span></c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </section>
    </main>
</div>
</body>
</html>
