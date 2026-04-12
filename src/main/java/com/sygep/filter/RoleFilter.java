package com.sygep.filter;

import com.sygep.entity.User;
import com.sygep.entity.UserRole;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class RoleFilter extends HttpFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);
        User currentUser = session == null ? null : (User) session.getAttribute("currentUser");

        if (currentUser == null || !currentUser.isActif()) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        if (!isAuthorized(httpRequest, currentUser.getRole())) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Acces refuse pour ce role.");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isAuthorized(HttpServletRequest request, UserRole role) {
        String relativePath = request.getRequestURI().substring(request.getContextPath().length());
        if (relativePath.startsWith("/app/admin/")) {
            return role == UserRole.ADMIN;
        }
        if (relativePath.startsWith("/app/coordinator/")) {
            return role == UserRole.COORDINATEUR;
        }
        if (relativePath.startsWith("/app/supervisor/")) {
            return role == UserRole.ENCADREUR;
        }
        if (relativePath.startsWith("/app/student/")) {
            return role == UserRole.ETUDIANT;
        }
        return true;
    }
}
