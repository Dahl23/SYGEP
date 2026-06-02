package com.sygep.servlet;

import com.sygep.ejb.AuthService;
import com.sygep.entity.User;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class AuthServlet extends BaseServlet {

    @EJB
    private AuthService authService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if ("/logout".equals(request.getServletPath())) {
            logout(request, response);
            return;
        }

        authService.ensureDemoUsers();
        exposeFlash(request);
        if (currentUser(request) != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if ("/logout".equals(request.getServletPath())) {
            logout(request, response);
            return;
        }

        authService.ensureDemoUsers();
        String email = request.getParameter("email");
        String motDePasse = request.getParameter("motDePasse");

        User user = authService.authenticate(email, motDePasse);
        if (user == null) {
            request.setAttribute("error", "Identifiants invalides ou compte inactif.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("currentUser", user);
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }

    private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/login");
    }
}
