package com.sygep.servlet;

import com.sygep.ejb.ProjectService;
import com.sygep.entity.Project;
import com.sygep.entity.ProjectDocument;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.util.UUID;

@MultipartConfig(
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 10 * 1024 * 1024,
        fileSizeThreshold = 1024 * 1024
)
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
            Project project = projectService.findProject(projectId);
            if (!project.getStudent().getId().equals(currentUserId(request))) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Projet non accessible.");
                return;
            }
            request.setAttribute("project", project);
            request.setAttribute("projectId", project.getId());
        } else if ("/document/download".equals(path(request))) {
            downloadDocument(request, response);
            return;
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

            Long projectId;
            if ("/update".equals(path)) {
                projectId = parseLong(request.getParameter("projectId"));
                projectService.updateProject(projectId, currentUserId(request), titre, resume, keywords);
                flash(request, "success", "Projet modifie et soumis a nouveau.");
            } else if ("/document/delete".equals(path)) {
                projectId = parseLong(request.getParameter("projectId"));
                ProjectDocument document = projectService.findDocument(parseLong(request.getParameter("documentId")));
                projectService.deleteDocument(document.getId(), currentUserId(request));
                java.nio.file.Files.deleteIfExists(uploadDirectory().resolve(document.getStoredName()));
                flash(request, "success", "Piece jointe supprimee.");
            } else {
                Project project = projectService.submitProject(currentUserId(request), titre, resume, keywords);
                projectId = project.getId();
                flash(request, "success", "Projet soumis avec analyse IA.");
            }

            if (!"/document/delete".equals(path)) {
                attachDocumentIfPresent(request, projectId);
            }
            response.sendRedirect(request.getContextPath() + "/projects");
        } catch (RuntimeException exception) {
            request.setAttribute("error", exception.getMessage());
            exposeFormValues(request, titre, resume, keywords);
            request.setAttribute("projectId", request.getParameter("projectId"));
            request.getRequestDispatcher("/WEB-INF/views/submit-project.jsp").forward(request, response);
        }
    }

    private void downloadDocument(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long documentId = parseLong(request.getParameter("id"));
        ProjectDocument document = projectService.findDocument(documentId);
        if (document == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Document introuvable.");
            return;
        }
        java.nio.file.Path file = uploadDirectory().resolve(document.getStoredName());
        if (!java.nio.file.Files.exists(file)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Fichier absent du serveur.");
            return;
        }
        response.setContentType(document.getContentType() == null ? "application/octet-stream" : document.getContentType());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + document.getFileName().replace("\"", "") + "\"");
        java.nio.file.Files.copy(file, response.getOutputStream());
    }

    private void attachDocumentIfPresent(HttpServletRequest request, Long projectId) throws IOException, ServletException {
        Part part = request.getPart("document");
        if (part == null || part.getSize() <= 0) {
            return;
        }
        String originalName = part.getSubmittedFileName();
        if (originalName == null || originalName.isBlank()) {
            return;
        }
        String storedName = UUID.randomUUID() + "-" + originalName;
        java.nio.file.Path uploadDir = uploadDirectory();
        java.nio.file.Files.createDirectories(uploadDir);
        java.nio.file.Files.copy(part.getInputStream(), uploadDir.resolve(storedName));
        projectService.addDocument(projectId, currentUserId(request), originalName, storedName,
                part.getContentType(), part.getSize());
    }

    private java.nio.file.Path uploadDirectory() {
        String configured = System.getenv("SYGEP_UPLOAD_DIR");
        if (configured != null && !configured.isBlank()) {
            return java.nio.file.Path.of(configured);
        }
        return java.nio.file.Path.of(System.getProperty("user.home"), "sygep-uploads");
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
