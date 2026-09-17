<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Validation - SYGEP</title>
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
                <a href="${pageContext.request.contextPath}/projects">Tous les projets</a>
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
            <h1>Validation et affectation</h1>
            <p>Valider, rejeter ou affecter les projets aux superviseurs.</p>
        </section>

        <c:if test="${not empty success}">
            <div class="message success">${success}</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="message error">${error}</div>
        </c:if>

        <section class="panel section">
            <h2>Projets a traiter</h2>
            <c:choose>
                <c:when test="${empty projects}">
                    <p class="muted">Aucun projet en attente.</p>
                </c:when>
                <c:otherwise>
                    <div class="list">
                        <c:forEach var="project" items="${projects}">
                            <article class="item">
                                <h3>${project.titre}
                                    <span class="badge ${project.statutName}" style="margin-left:.5rem;vertical-align:2px;">${project.statut.label}</span>
                                </h3>
                                <p class="item-meta">
                                    Etudiant : ${project.student.displayName} &middot; soumis le ${project.submittedAtText}
                                </p>
                                <c:if test="${not empty project.keywords}">
                                    <div class="chips">
                                        <span class="chip">${project.keywords}</span>
                                    </div>
                                </c:if>
                                <div class="resume-box">${project.resume}</div>

                                <div class="actions" style="margin-top:.7rem;">
                                    <c:choose>
                                        <c:when test="${project.hasDocument()}">
                                            <a class="doc-link" href="${pageContext.request.contextPath}/project/download?id=${project.id}">
                                                <span class="doc-tag">${project.documentKind}</span> Ouvrir le document
                                            </a>
                                        </c:when>
                                        <c:otherwise><span class="muted">Aucun document joint</span></c:otherwise>
                                    </c:choose>
                                    <a class="button secondary small" href="${pageContext.request.contextPath}/project/view?id=${project.id}">Fiche complete</a>
                                </div>

                                <div class="actions" style="margin-top:.7rem;border-top:1px solid var(--border);padding-top:.7rem;">
                                    <form method="post" action="${pageContext.request.contextPath}/admin/validate">
                                        <input type="hidden" name="projectId" value="${project.id}">
                                        <select name="supervisorId" style="width:auto;min-width:220px;">
                                            <option value="">Valider sans affectation</option>
                                            <c:forEach var="supervisor" items="${supervisors}">
                                                <option value="${supervisor.id}">${supervisor.displayName}</option>
                                            </c:forEach>
                                        </select>
                                        <button type="submit">Valider</button>
                                    </form>

                                    <form method="post" action="${pageContext.request.contextPath}/admin/assign">
                                        <input type="hidden" name="projectId" value="${project.id}">
                                        <select name="supervisorId" required style="width:auto;min-width:220px;">
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

                                    <span class="spacer"></span>

                                    <form method="post" action="${pageContext.request.contextPath}/admin/reject">
                                        <input type="hidden" name="projectId" value="${project.id}">
                                        <button class="danger" type="submit">Rejeter</button>
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
                <div class="table-wrap">
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
                                <td>
                                    <span class="cell-main">${user.displayName}</span>
                                    <div class="cell-sub">${user.email}</div>
                                </td>
                                <td><span class="role-tag ${user.roleName}">${user.roleName}</span></td>
                                <td><c:choose><c:when test="${user.actif}"><span class="badge VALIDATED">Actif</span></c:when><c:otherwise><span class="badge REJECTED">Inactif</span></c:otherwise></c:choose></td>
                                <td>
                                    <form method="post" action="${pageContext.request.contextPath}/admin/users/toggle">
                                        <input type="hidden" name="userId" value="${user.id}">
                                        <button class="secondary small" type="submit">Basculer</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </article>
        </section>
    </main>
</div>
</body>
</html>