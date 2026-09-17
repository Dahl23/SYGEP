package com.sygep.servlet;

import com.sygep.ejb.ProjectService;
import com.sygep.entity.Project;
import com.sygep.entity.Role;
import com.sygep.entity.User;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class ProjectFileServlet extends BaseServlet {

    @EJB
    private ProjectService projectService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        exposeFlash(request);

        String path = pathInfo(request);
        Long projectId = parseLong(request.getParameter("id"));

        if ("/download".equals(path)) {
            download(request, response, projectId);
            return;
        }

        view(request, response, projectId);
    }

    private void view(HttpServletRequest request, HttpServletResponse response, Long projectId)
            throws ServletException, IOException {
        Project project = projectService.findProjectWithDetails(projectId);
        User user = currentUser(request);
        if (!canAccess(project, user)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Projet non accessible.");
            return;
        }
        request.setAttribute("project", project);
        request.getRequestDispatcher("/WEB-INF/views/project-details.jsp").forward(request, response);
    }

    private void download(HttpServletRequest request, HttpServletResponse response, Long projectId)
            throws IOException {
        if (projectId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Projet invalide.");
            return;
        }
        Project project = projectService.findProjectDocument(projectId);
        User user = currentUser(request);
        if (!canAccess(project, user)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Projet non accessible.");
            return;
        }
        if (!project.hasDocument() || project.getDocumentData() == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Aucun document joint a ce projet.");
            return;
        }

        String contentType = project.getDocumentContentType();
        if (contentType == null || contentType.isBlank()) {
            contentType = "application/octet-stream";
        }
        response.setContentType(contentType);
        response.setContentLengthLong(project.getDocumentSize() == null ? project.getDocumentData().length : project.getDocumentSize());

        boolean inline = "application/pdf".equalsIgnoreCase(contentType);
        String disposition = (inline ? "inline" : "attachment") + "; filename=\"" + project.getDocumentFileName() + "\"";
        response.setHeader("Content-Disposition", disposition);

        try (OutputStream output = response.getOutputStream()) {
            output.write(project.getDocumentData());
        }
    }

    private boolean canAccess(Project project, User user) {
        if (user == null) {
            return false;
        }
        if (user.getRole() == Role.ADMIN) {
            return true;
        }
        if (project.getStudent().getId().equals(user.getId())) {
            return true;
        }
        return project.getSupervisor() != null && project.getSupervisor().getId().equals(user.getId());
    }

    private String pathInfo(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        return pathInfo == null ? "/view" : pathInfo;
    }
}