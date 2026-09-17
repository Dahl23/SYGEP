package com.sygep.servlet;

import com.sygep.ejb.EvaluationService;
import com.sygep.ejb.ProjectService;
import com.sygep.entity.Project;
import com.sygep.util.Pagination;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

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

        List<Project> allProjects = projectService.findProjectsBySupervisor(supervisorId);
        List<Project> allReady = evaluationService.findProjectsReadyForEvaluation(supervisorId);
        List<Project> allEvaluated = evaluationService.findEvaluatedProjectsByEvaluator(supervisorId);

        int page = pageOf(request, "page");
        int readyPage = pageOf(request, "readyPage");
        int evaluatedPage = pageOf(request, "evaluatedPage");

        request.setAttribute("projects", Pagination.page(allProjects, page, Pagination.DEFAULT_PAGE_SIZE));
        request.setAttribute("projectsPage", page);
        request.setAttribute("projectsTotalPages", Pagination.totalPages(allProjects.size(), Pagination.DEFAULT_PAGE_SIZE));

        request.setAttribute("readyProjects", Pagination.page(allReady, readyPage, Pagination.DEFAULT_PAGE_SIZE));
        request.setAttribute("readyPage", readyPage);
        request.setAttribute("readyTotalPages", Pagination.totalPages(allReady.size(), Pagination.DEFAULT_PAGE_SIZE));

        request.setAttribute("evaluatedProjects", Pagination.page(allEvaluated, evaluatedPage, Pagination.DEFAULT_PAGE_SIZE));
        request.setAttribute("evaluatedPage", evaluatedPage);
        request.setAttribute("evaluatedTotalPages", Pagination.totalPages(allEvaluated.size(), Pagination.DEFAULT_PAGE_SIZE));

        Long selectedProjectId = parseLong(request.getParameter("projectId"));
        if (selectedProjectId != null) {
            Project selectedProject = projectService.findProjectWithDetails(selectedProjectId);
            if (selectedProject.getSupervisor() == null || !selectedProject.getSupervisor().getId().equals(supervisorId)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Projet non affecte a ce superviseur.");
                return;
            }
            request.setAttribute("selectedProject", selectedProject);
            exposeEditTargets(request, supervisorId);
        }

        request.getRequestDispatcher("/WEB-INF/views/supervisor-dashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Long projectId = parseLong(request.getParameter("projectId"));
        Long userId = currentUserId(request);

        try {
            switch (path(request)) {
                case "/comment/update":
                    projectService.updateSupervisorComment(
                            parseLong(request.getParameter("commentId")),
                            userId,
                            request.getParameter("comment"));
                    flash(request, "success", "Commentaire mis a jour.");
                    break;
                case "/comment/delete":
                    projectService.deleteSupervisorComment(parseLong(request.getParameter("commentId")), userId);
                    flash(request, "success", "Commentaire supprime.");
                    break;
                case "/progress/update":
                    projectService.updateProgressReport(
                            parseLong(request.getParameter("reportId")),
                            userId,
                            request.getParameter("title"),
                            request.getParameter("content"),
                            parseInteger(request.getParameter("progressPercent")));
                    flash(request, "success", "Rapport de suivi mis a jour.");
                    break;
                case "/progress/delete":
                    projectService.deleteProgressReport(parseLong(request.getParameter("reportId")), userId);
                    flash(request, "success", "Rapport de suivi supprime.");
                    break;
                case "/progress":
                    addProgressReport(request, projectId, userId);
                    break;
                default:
                    addComment(request, projectId, userId);
            }
        } catch (RuntimeException exception) {
            flash(request, "error", exception.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/supervisor/dashboard?projectId=" + projectId);
    }

    private void addComment(HttpServletRequest request, Long projectId, Long userId) {
        projectService.addSupervisorComment(projectId, userId, request.getParameter("comment"));
        flash(request, "success", "Commentaire ajoute.");
    }

    private void addProgressReport(HttpServletRequest request, Long projectId, Long userId) {
        projectService.addProgressReport(
                projectId,
                userId,
                request.getParameter("title"),
                request.getParameter("content"),
                parseInteger(request.getParameter("progressPercent")));
        flash(request, "success", "Suivi ajoute.");
    }

    private void exposeEditTargets(HttpServletRequest request, Long supervisorId) {
        Long editCommentId = parseLong(request.getParameter("editCommentId"));
        if (editCommentId != null) {
            request.setAttribute("editComment", projectService.findSupervisorComment(editCommentId, supervisorId));
        }
        Long editReportId = parseLong(request.getParameter("editReportId"));
        if (editReportId != null) {
            request.setAttribute("editReport", projectService.findProgressReport(editReportId, supervisorId));
        }
    }

    private int pageOf(HttpServletRequest request, String param) {
        Integer value = parseInteger(request.getParameter(param));
        return value == null || value < 1 ? 1 : value;
    }

    private String path(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        return pathInfo == null ? "/dashboard" : pathInfo;
    }
}