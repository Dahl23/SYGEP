<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Evaluation - SYGEP</title>
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
                <c:if test="${sessionScope.currentUser.roleName == 'SUPERVISOR'}">
                    <a href="${pageContext.request.contextPath}/supervisor/dashboard">Supervision</a>
                </c:if>
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
            <h1>Evaluation du projet</h1>
            <p>Note finale calculee automatiquement : technique 40 %, documentation 30 %, presentation 30 %.</p>
        </section>

<c:if test="${not empty evaluation}">
            <div class="message success">Projet deja evalue (note actuelle : <strong>${evaluation.finalScoreFormatted}/20</strong>). Cette page met a jour l'evaluation existante et re-archive le projet.</div>
        </c:if>
        <c:if test="${not empty project.evaluation}">
            <div class="message muted">Note finale actuelle : <strong>${project.evaluation.finalScoreFormatted}/20</strong> (appliquee au projet).</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="message error">${error}</div>
        </c:if>

        <section class="grid two">
            <article class="panel">
                <div class="actions" style="justify-content:space-between;">
                    <h2 style="margin:0;">${project.titre}</h2>
                    <span class="badge ${project.statutName}">${project.statut.label}</span>
                </div>
                <div class="detail-grid" style="margin-top:.8rem;">
                    <div class="detail-field"><span class="label">Etudiant</span><div class="value">${project.student.displayName}</div></div>
                    <div class="detail-field"><span class="label">Soumission</span><div class="value">${project.submittedAtText}</div></div>
                </div>
                <div class="resume-box" style="margin-top:.8rem;">${project.resume}</div>
                <div class="actions" style="margin-top:.8rem;">
                    <c:choose>
                        <c:when test="${project.hasDocument()}">
                            <a class="doc-link" href="${pageContext.request.contextPath}/project/download?id=${project.id}">
                                <span class="doc-tag">${project.documentKind}</span> Ouvrir le document
                            </a>
                        </c:when>
                        <c:otherwise><span class="muted">Aucun document joint</span></c:otherwise>
                    </c:choose>
                </div>
                <c:if test="${not empty project.evaluation}">
                    <div style="border-top:1px solid var(--border);margin-top:1rem;padding-top:.8rem;">
                        <p style="margin:.2rem 0;"><strong>Note finale actuelle :</strong> ${project.evaluation.finalScoreFormatted}/20</p>
                        <p class="muted" style="margin:.2rem 0;">Evaluee le ${project.evaluation.evaluatedAtText}</p>
                    </div>
                </c:if>
            </article>

            <form class="panel stack" method="post" action="${pageContext.request.contextPath}/evaluation/save"
                  oninput="previewScore()">
                <input type="hidden" name="projectId" value="${project.id}">

                <label>Note technique /20
                    <input name="technicalScore" class="weight-20" type="number" min="0" max="20" step="0.25"
                           value="${evaluation.technicalScore}" placeholder="0.0 - 20.0" required>
                    <span class="hint">Poids 40%</span>
                </label>

                <label>Note documentation /20
                    <input name="documentationScore" class="weight-20" type="number" min="0" max="20" step="0.25"
                           value="${evaluation.documentationScore}" placeholder="0.0 - 20.0" required>
                    <span class="hint">Poids 30%</span>
                </label>

                <label>Note presentation /20
                    <input name="presentationScore" class="weight-20" type="number" min="0" max="20" step="0.25"
                           value="${evaluation.presentationScore}" placeholder="0.0 - 20.0" required>
                    <span class="hint">Poids 30%</span>
                </label>

                <label>Feedback
                    <textarea name="feedback" placeholder="Appreciation generale, points forts, axes d'amelioration...">${evaluation.feedback}</textarea>
                </label>

                <div class="panel" style="background:#f0fdfa;border:1px solid #99f6e4;">
                    <strong>Note finale calculee :</strong>
                    <span id="finalPreview" style="font-size:1.4rem;font-weight:800;color:var(--primary-dark);">-</span>
                    <span style="color:var(--muted);">/20</span>
                </div>

                <c:choose>
                    <c:when test="${not empty evaluation}">
                        <button type="submit">Mettre a jour l'evaluation</button>
                    </c:when>
                    <c:otherwise>
                        <button type="submit">Enregistrer et archiver</button>
                    </c:otherwise>
                </c:choose>
            </form>
        </section>
    </main>
</div>

<script>
    function previewScore() {
        const form = document.querySelector('form.panel.stack');
        const parse = (name) => {
            const input = form.querySelector('input[name="' + name + '"]');
            const value = parseFloat((input ? input.value : '').replace(',', '.'));
            return isNaN(value) ? 0 : value;
        };
        const total = parse('technicalScore') * 0.4 + parse('documentationScore') * 0.3 + parse('presentationScore') * 0.3;
        const preview = document.getElementById('finalPreview');
        preview.textContent = total.toFixed(2).replace('.', ',');
    }
    previewScore();
</script>
</body>
</html>