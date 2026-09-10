package com.sygep.servlet;

import com.sygep.ejb.AdminService;
import com.sygep.entity.Project;
import com.sygep.entity.ProjectStatus;
import com.sygep.entity.Role;
import com.sygep.util.Pagination;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class AdminServlet extends BaseServlet {

    @EJB
    private AdminService adminService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        exposeFlash(request);
        loadValidationPage(request);
        request.getRequestDispatcher("/WEB-INF/views/validate-projects.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = path(request);

        try {
            if ("/validate".equals(path)) {
                adminService.validateProject(
                        parseLong(request.getParameter("projectId")),
                        parseLong(request.getParameter("supervisorId"))
                );
                flash(request, "success", "Projet valide.");
            } else if ("/reject".equals(path)) {
                adminService.rejectProject(parseLong(request.getParameter("projectId")));
                flash(request, "success", "Projet rejete.");
            } else if ("/assign".equals(path)) {
                adminService.assignSupervisor(
                        parseLong(request.getParameter("projectId")),
                        parseLong(request.getParameter("supervisorId"))
                );
                flash(request, "success", "Superviseur affecte.");
            } else if ("/users/create".equals(path)) {
                adminService.createUser(
                        request.getParameter("fullName"),
                        request.getParameter("email"),
                        request.getParameter("motDePasse"),
                        Role.valueOf(request.getParameter("role"))
                );
                flash(request, "success", "Utilisateur cree.");
            } else if ("/users/toggle".equals(path)) {
                adminService.toggleActive(parseLong(request.getParameter("userId")));
                flash(request, "success", "Statut utilisateur mis a jour.");
            }
        } catch (RuntimeException exception) {
            flash(request, "error", exception.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/projects");
    }

    private void loadValidationPage(HttpServletRequest request) {
        String statusParam = request.getParameter("status");
        ProjectStatus filter = null;
        if (statusParam != null && !statusParam.isBlank()) {
            try {
                filter = ProjectStatus.valueOf(statusParam);
            } catch (IllegalArgumentException ignored) {
                filter = null;
            }
        }

        List<Project> allProjects = adminService.findProjectsForValidation(filter);
        int page = pageOf(request.getParameter("page"));

        List<com.sygep.entity.User> allUsers = adminService.findUsers();
        int usersPage = pageOf(request.getParameter("usersPage"));

        request.setAttribute("statusFilter", filter);
        request.setAttribute("allStatuses", ProjectStatus.values());
        request.setAttribute("projects", Pagination.page(allProjects, page, Pagination.DEFAULT_PAGE_SIZE));
        request.setAttribute("page", page);
        request.setAttribute("totalPages", Pagination.totalPages(allProjects.size(), Pagination.DEFAULT_PAGE_SIZE));
        request.setAttribute("totalProjects", allProjects.size());
        request.setAttribute("supervisors", adminService.findSupervisors());
        request.setAttribute("users", Pagination.page(allUsers, usersPage, Pagination.DEFAULT_PAGE_SIZE));
        request.setAttribute("usersPage", usersPage);
        request.setAttribute("usersTotalPages", Pagination.totalPages(allUsers.size(), Pagination.DEFAULT_PAGE_SIZE));
        request.setAttribute("roles", Role.values());
    }

    private int pageOf(String value) {
        Integer parsed = parseInteger(value);
        return parsed == null || parsed < 1 ? 1 : parsed;
    }

    private String path(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        return pathInfo == null ? "/projects" : pathInfo;
    }
}
