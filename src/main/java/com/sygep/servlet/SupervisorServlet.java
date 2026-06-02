package com.sygep.servlet;

import com.sygep.ejb.EvaluationService;
import com.sygep.ejb.ProjectService;
import com.sygep.entity.Project;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SupervisorServlet extends BaseServlet {

    @EJB
    private ProjectService projectService;

    @EJB
    private EvaluationService evaluationService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        exposeFlash(request);
        Long supervisorId = currentUserId(request);

        request.setAttribute("projects", projectService.findProjectsBySupervisor(supervisorId));
        request.setAttribute("readyProjects", evaluationService.findProjectsReadyForEvaluation(supervisorId));

        Long selectedProjectId = parseLong(request.getParameter("projectId"));
        if (selectedProjectId != null) {
            Project selectedProject = projectService.findProjectWithDetails(selectedProjectId);
            if (selectedProject.getSupervisor() == null || !selectedProject.getSupervisor().getId().equals(supervisorId)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Projet non affecte a ce superviseur.");
                return;
            }
            request.setAttribute("selectedProject", selectedProject);
        }

        request.getRequestDispatcher("/WEB-INF/views/supervisor-dashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Long projectId = parseLong(request.getParameter("projectId"));

        try {
            if ("/progress".equals(path(request))) {
                projectService.addProgressReport(
                        projectId,
                        currentUserId(request),
                        request.getParameter("title"),
                        request.getParameter("content"),
                        parseInteger(request.getParameter("progressPercent"))
                );
                flash(request, "success", "Suivi ajoute.");
            } else {
                projectService.addSupervisorComment(projectId, currentUserId(request), request.getParameter("comment"));
                flash(request, "success", "Commentaire ajoute.");
            }
        } catch (RuntimeException exception) {
            flash(request, "error", exception.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/supervisor/dashboard?projectId=" + projectId);
    }

    private String path(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        return pathInfo == null ? "/dashboard" : pathInfo;
    }
}
