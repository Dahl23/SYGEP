package com.sygep.filter;

import com.sygep.ejb.AuthService;
import com.sygep.entity.Role;
import com.sygep.entity.User;
import jakarta.ejb.EJB;
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

    @EJB
    private AuthService authService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);
        User currentUser = session == null ? null : (User) session.getAttribute("currentUser");

        String relativePath = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        if (isPublicPath(relativePath)) {
            chain.doFilter(request, response);
            return;
        }

        if (currentUser != null) {
            currentUser = authService.findUser(currentUser.getId());
            if (session != null) {
                session.setAttribute("currentUser", currentUser);
            }
        }

        if (currentUser == null || !currentUser.isActif()) {
            if (session != null) {
                session.invalidate();
            }
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        if (!isAuthorized(httpRequest, currentUser.getRole())) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Acces refuse pour ce role.");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicPath(String relativePath) {
        return relativePath.equals("/")
                || relativePath.equals("/index.jsp")
                || relativePath.equals("/login")
                || relativePath.equals("/logout")
                || relativePath.startsWith("/assets/");
    }

    private boolean isAuthorized(HttpServletRequest request, Role role) {
        String relativePath = request.getRequestURI().substring(request.getContextPath().length());
        if (relativePath.startsWith("/admin/")) {
            return role == Role.ADMIN;
        }
        if (relativePath.startsWith("/proposal/")) {
            return role == Role.STUDENT;
        }
        if (relativePath.startsWith("/supervisor/")) {
            return role == Role.SUPERVISOR;
        }
        if (relativePath.startsWith("/evaluation/")) {
            return role == Role.SUPERVISOR || role == Role.ADMIN;
        }
        return true;
    }
}
