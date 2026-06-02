package com.sygep.servlet;

import com.sygep.entity.User;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public abstract class BaseServlet extends HttpServlet {

    protected User currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (User) session.getAttribute("currentUser");
    }

    protected Long currentUserId(HttpServletRequest request) {
        User user = currentUser(request);
        return user == null ? null : user.getId();
    }

    protected Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Long.valueOf(value);
    }

    protected Integer parseInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Integer.valueOf(value);
    }

    protected void flash(HttpServletRequest request, String type, String message) {
        request.getSession(true).setAttribute("flash_" + type, message);
    }

    protected void exposeFlash(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }

        moveFlash(session, request, "success");
        moveFlash(session, request, "error");
    }

    private void moveFlash(HttpSession session, HttpServletRequest request, String type) {
        String key = "flash_" + type;
        Object value = session.getAttribute(key);
        if (value != null) {
            request.setAttribute(type, value);
            session.removeAttribute(key);
        }
    }
}
