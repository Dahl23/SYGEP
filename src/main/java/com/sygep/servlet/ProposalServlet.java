package com.sygep.servlet;

import com.sygep.ejb.ProjectService;
import com.sygep.entity.Project;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ProposalServlet extends BaseServlet {

    @EJB
    private ProjectService projectService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        exposeFlash(request);

        if ("/edit".equals(path(request))) {
            Long projectId = parseLong(request.getParameter("id"));
            Project project = projectService.findProjectWithDetails(projectId);
            if (!project.getStudent().getId().equals(currentUserId(request))) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Projet non accessible.");
                return;
            }
            request.setAttribute("project", project);
        }

        request.getRequestDispatcher("/WEB-INF/views/submit-project.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = path(request);
        String titre = request.getParameter("titre");
        String resume = request.getParameter("resume");
        String keywords = request.getParameter("keywords");

        try {
            if ("/analyze".equals(path)) {
                request.setAttribute("analysis", projectService.analyzeDraft(titre, resume));
                exposeFormValues(request, titre, resume, keywords);
                request.getRequestDispatcher("/WEB-INF/views/submit-project.jsp").forward(request, response);
                return;
            }

            if ("/update".equals(path)) {
                Long projectId = parseLong(request.getParameter("projectId"));
                projectService.updateProject(projectId, currentUserId(request), titre, resume, keywords);
                flash(request, "success", "Projet modifie et soumis a nouveau.");
            } else {
                projectService.submitProject(currentUserId(request), titre, resume, keywords);
                flash(request, "success", "Projet soumis avec analyse IA.");
            }

            response.sendRedirect(request.getContextPath() + "/projects");
        } catch (RuntimeException exception) {
            request.setAttribute("error", exception.getMessage());
            exposeFormValues(request, titre, resume, keywords);
            request.setAttribute("projectId", request.getParameter("projectId"));
            request.getRequestDispatcher("/WEB-INF/views/submit-project.jsp").forward(request, response);
        }
    }

    private String path(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        return pathInfo == null ? "/new" : pathInfo;
    }

    private void exposeFormValues(HttpServletRequest request, String titre, String resume, String keywords) {
        request.setAttribute("formTitre", titre);
        request.setAttribute("formResume", resume);
        request.setAttribute("formKeywords", keywords);
    }
}
