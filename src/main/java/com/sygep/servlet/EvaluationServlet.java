package com.sygep.servlet;

import com.sygep.ejb.EvaluationService;
import com.sygep.entity.Project;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

public class EvaluationServlet extends BaseServlet {

    @EJB
    private EvaluationService evaluationService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        exposeFlash(request);

        Long projectId = parseLong(request.getParameter("projectId"));
        Project project = evaluationService.findProjectForEvaluation(projectId, currentUserId(request));
        request.setAttribute("project", project);
        request.setAttribute("evaluation", evaluationService.findEvaluationByProject(projectId));
        request.getRequestDispatcher("/WEB-INF/views/evaluate-project.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Long projectId = parseLong(request.getParameter("projectId"));

        try {
            boolean existing = evaluationService.findEvaluationByProject(projectId) != null;
            if (existing) {
                evaluationService.reEvaluateProject(
                        projectId,
                        currentUserId(request),
                        parseScore(request.getParameter("technicalScore")),
                        parseScore(request.getParameter("documentationScore")),
                        parseScore(request.getParameter("presentationScore")),
                        request.getParameter("feedback")
                );
                flash(request, "success", "Evaluation mise a jour.");
            } else {
                evaluationService.evaluateProject(
                        projectId,
                        currentUserId(request),
                        parseScore(request.getParameter("technicalScore")),
                        parseScore(request.getParameter("documentationScore")),
                        parseScore(request.getParameter("presentationScore")),
                        request.getParameter("feedback")
                );
                flash(request, "success", "Evaluation enregistree. Le projet est archive automatiquement.");
            }
            response.sendRedirect(request.getContextPath() + "/supervisor/dashboard");
        } catch (RuntimeException exception) {
            request.setAttribute("error", exception.getMessage());
            Project project = evaluationService.findProjectForEvaluation(projectId, currentUserId(request));
            request.setAttribute("project", project);
            request.getRequestDispatcher("/WEB-INF/views/evaluate-project.jsp").forward(request, response);
        }
    }

    private BigDecimal parseScore(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return new BigDecimal(value.replace(',', '.'));
    }
}
