<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Validation - SYGEP</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sygep.css">
</head>
<body>
<div class="page">
    <header class="topbar">
        <div class="topbar-inner">
            <a class="brand" href="${pageContext.request.contextPath}/dashboard">SYGEP</a>
            <nav class="nav">
                <a href="${pageContext.request.contextPath}/dashboard">Tableau de bord</a>
                <a href="${pageContext.request.contextPath}/projects">Tous les projets</a>
                <a href="${pageContext.request.contextPath}/logout">Deconnexion</a>
            </nav>
        </div>
    </header>

    <main class="container">
        <section class="section">
            <h1>Validation et affectation</h1>
            <p class="muted">Valider, rejeter ou affecter les projets aux superviseurs.</p>
        </section>

        <c:if test="${not empty success}">
            <div class="message success">${success}</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="message error">${error}</div>
        </c:if>

        <section class="section panel">
            <h2>Projets a traiter</h2>
            <c:choose>
                <c:when test="${empty projects}">
                    <p class="muted">Aucun projet en attente.</p>
                </c:when>
                <c:otherwise>
                    <div class="list">
                        <c:forEach var="project" items="${projects}">
                            <article class="item">
                                <h3>${project.titre}</h3>
                                <p class="muted">${project.student.displayName} - <span class="badge ${project.statutName}">${project.statut.label}</span></p>
                                <p>${project.resume}</p>
                                <p><strong>Score IA :</strong>
                                    <c:choose>
                                        <c:when test="${not empty project.aiScore}">${project.aiScore}/100</c:when>
                                        <c:otherwise>Non analyse</c:otherwise>
                                    </c:choose>
                                </p>
                                <c:if test="${not empty project.aiSuggestions}">
                                    <pre>${project.aiSuggestions}</pre>
                                </c:if>

                                <div class="actions">
                                    <form method="post" action="${pageContext.request.contextPath}/admin/validate">
                                        <input type="hidden" name="projectId" value="${project.id}">
                                        <select name="supervisorId">
                                            <option value="">Valider sans affectation</option>
                                            <c:forEach var="supervisor" items="${supervisors}">
                                                <option value="${supervisor.id}">${supervisor.displayName}</option>
                                            </c:forEach>
                                        </select>
                                        <button type="submit">Valider</button>
                                    </form>

                                    <form method="post" action="${pageContext.request.contextPath}/admin/reject">
                                        <input type="hidden" name="projectId" value="${project.id}">
                                        <button class="danger" type="submit">Rejeter</button>
                                    </form>

                                    <form method="post" action="${pageContext.request.contextPath}/admin/assign">
                                        <input type="hidden" name="projectId" value="${project.id}">
                                        <select name="supervisorId" required>
                                            <option value="">Choisir superviseur</option>
                                            <c:forEach var="supervisor" items="${supervisors}">
                                                <c:choose>
                                                    <c:when test="${not empty project.supervisor and project.supervisor.id == supervisor.id}">
                                                        <option value="${supervisor.id}" selected="selected">${supervisor.displayName}</option>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <option value="${supervisor.id}">${supervisor.displayName}</option>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:forEach>
                                        </select>
                                        <button class="secondary" type="submit">Affecter</button>
                                    </form>
                                </div>
                            </article>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>

        <section class="grid two">
            <article class="panel">
                <h2>Creer un utilisateur</h2>
                <form class="stack" method="post" action="${pageContext.request.contextPath}/admin/users/create">
                    <label>Nom complet
                        <input name="fullName" required>
                    </label>
                    <label>Email
                        <input name="email" type="email" required>
                    </label>
                    <label>Mot de passe
                        <input name="motDePasse" type="password" required>
                    </label>
                    <label>Role
                        <select name="role" required>
                            <c:forEach var="role" items="${roles}">
                                <option value="${role}">${role.label}</option>
                            </c:forEach>
                        </select>
                    </label>
                    <button type="submit">Creer</button>
                </form>
            </article>

            <article class="panel">
                <h2>Utilisateurs</h2>
                <table>
                    <thead>
                    <tr>
                        <th>Nom</th>
                        <th>Role</th>
                        <th>Etat</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="user" items="${users}">
                        <tr>
                            <td>${user.displayName}<br><span class="muted">${user.email}</span></td>
                            <td>${user.role.label}</td>
                            <td><c:choose><c:when test="${user.actif}">Actif</c:when><c:otherwise>Inactif</c:otherwise></c:choose></td>
                            <td>
                                <form method="post" action="${pageContext.request.contextPath}/admin/users/toggle">
                                    <input type="hidden" name="userId" value="${user.id}">
                                    <button class="secondary" type="submit">Basculer</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </article>
        </section>
    </main>
</div>
</body>
</html>
