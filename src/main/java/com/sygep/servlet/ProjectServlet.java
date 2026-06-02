package com.sygep.servlet;

import com.sygep.ejb.ProjectService;
import com.sygep.entity.Project;
import com.sygep.entity.ProjectStatus;
import com.sygep.entity.User;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ProjectServlet extends BaseServlet {

    @EJB
    private ProjectService projectService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        exposeFlash(request);

        User user = currentUser(request);
        List<Project> projects = projectService.findVisibleProjects(user);
        request.setAttribute("projects", projects);
        request.setAttribute("statuses", ProjectStatus.values());

        if ("/dashboard".equals(request.getServletPath())) {
            request.setAttribute("statusCounts", countByStatus(projects));
            request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/projects.jsp").forward(request, response);
    }

    private Map<ProjectStatus, Long> countByStatus(List<Project> projects) {
        Map<ProjectStatus, Long> counts = new EnumMap<>(ProjectStatus.class);
        for (ProjectStatus status : ProjectStatus.values()) {
            counts.put(status, 0L);
        }
        for (Project project : projects) {
            counts.put(project.getStatut(), counts.get(project.getStatut()) + 1);
        }
        return counts;
    }
}
