package com.sygep.servlet;

import com.sygep.ejb.AdminService;
import com.sygep.entity.Role;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

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
        request.setAttribute("projects", adminService.findProjectsForValidation());
        request.setAttribute("supervisors", adminService.findSupervisors());
        request.setAttribute("users", adminService.findUsers());
        request.setAttribute("roles", Role.values());
    }

    private String path(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        return pathInfo == null ? "/projects" : pathInfo;
    }
}
