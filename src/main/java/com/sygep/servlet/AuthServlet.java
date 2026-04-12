package com.sygep.servlet;

import com.sygep.ejb.AdminService;
import com.sygep.entity.User;
import com.sygep.entity.UserRole;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class AuthServlet extends HttpServlet {

    @EJB
    private AdminService adminService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String motDePasse = request.getParameter("motDePasse");

        User user = adminService.authenticate(email, motDePasse);
        if (user == null) {
            request.setAttribute("error", "Identifiants invalides ou compte inactif.");
            request.getRequestDispatcher("/auth/login.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("currentUser", user);

        response.sendRedirect(request.getContextPath() + resolveLandingPage(user.getRole()));
    }

    private String resolveLandingPage(UserRole role) {
        switch (role) {
            case ADMIN:
                return "/app/admin/home.jsp";
            case COORDINATEUR:
                return "/app/coordinator/home.jsp";
            case ENCADREUR:
                return "/app/supervisor/home.jsp";
            case ETUDIANT:
            default:
                return "/app/student/home.jsp";
        }
    }
}
