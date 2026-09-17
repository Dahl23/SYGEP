package com.sygep.servlet;

import com.sygep.ejb.ProjectService;
import com.sygep.entity.Project;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@MultipartConfig(maxFileSize = 20 * 1024 * 1024, maxRequestSize = 25 * 1024 * 1024)
public class ProposalServlet extends BaseServlet {

    private static final long MAX_DOCUMENT_SIZE = 10L * 1024 * 1024;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("pdf", "doc", "docx");

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

        byte[] documentData = null;
        String documentFileName = null;
        String documentContentType = null;
        Long documentSize = null;

        try {
            Part document = request.getPart("document");
            if (document != null && document.getSize() > 0) {
                documentFileName = sanitizeFileName(document.getSubmittedFileName());
                validateDocument(documentFileName, document.getSize());
                documentData = readAll(document);
                documentContentType = document.getContentType();
                documentSize = document.getSize();
            } else if ("/submit".equals(path)) {
                throw new IllegalArgumentException("Le document PDF ou Word est obligatoire.");
            }

            if ("/update".equals(path)) {
                Long projectId = parseLong(request.getParameter("projectId"));
                projectService.updateProject(projectId, currentUserId(request), titre, resume, keywords,
                        documentData, documentFileName, documentContentType, documentSize);
                flash(request, "success", "Projet modifie et soumis a nouveau.");
            } else {
                projectService.submitProject(currentUserId(request), titre, resume, keywords,
                        documentData, documentFileName, documentContentType, documentSize);
                flash(request, "success", "Projet soumis avec succes.");
            }

            response.sendRedirect(request.getContextPath() + "/projects");
        } catch (RuntimeException exception) {
            String projectIdValue = request.getParameter("projectId");
            if (projectIdValue != null && !projectIdValue.isBlank()) {
                Project project = projectService.findProjectWithDetails(parseLong(projectIdValue));
                if (project != null && project.getStudent().getId().equals(currentUserId(request))) {
                    request.setAttribute("project", project);
                }
            }
            request.setAttribute("error", exception.getMessage());
            exposeFormValues(request, titre, resume, keywords);
            request.setAttribute("projectId", projectIdValue);
            request.getRequestDispatcher("/WEB-INF/views/submit-project.jsp").forward(request, response);
        }
    }

    private byte[] readAll(Part document) throws IOException {
        try (java.io.InputStream input = document.getInputStream()) {
            return input.readAllBytes();
        }
    }

    private void validateDocument(String fileName, long size) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("Le nom du document est invalide.");
        }
        String lower = fileName.toLowerCase(Locale.ROOT);
        boolean allowed = ALLOWED_EXTENSIONS.stream().anyMatch(ext -> lower.endsWith("." + ext));
        if (!allowed) {
            throw new IllegalArgumentException("Le document doit etre au format PDF, DOC ou DOCX.");
        }
        if (size > MAX_DOCUMENT_SIZE) {
            throw new IllegalArgumentException("Le document ne doit pas depasser 10 Mo.");
        }
    }

    private String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return null;
        }
        String cleaned = fileName.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
        return cleaned.isBlank() ? null : cleaned;
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