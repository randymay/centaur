package org.blaazin.centaur.web.user.filter;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import javax.servlet.*;
import java.io.IOException;

/**
 * Sets the Current User object as well as Login and Logout URLs to the Request
 */
public class CentaurUserFilter implements Filter {

    private String postLoginURL = null;
    private String postLogoutURL = null;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Nothing needed here.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        UserService userService = UserServiceFactory.getUserService();
        User currentUser = userService.getCurrentUser();

        request.setAttribute("loginURL", userService.createLoginURL(createPostLoginURL(request)));
        request.setAttribute("logoutURL", userService.createLogoutURL(createPostLogoutURL(request)));
        request.setAttribute("currentUser", currentUser);

        chain.doFilter(request, response);
    }

    public String createPostLoginURL(ServletRequest request) {
        if (postLoginURL == null) {
            postLoginURL = (request.getServletContext().getInitParameter("postLoginURL"));
        }

        return postLoginURL;
    }

    public String createPostLogoutURL(ServletRequest request) {
        if (postLogoutURL == null) {
            postLogoutURL = (request.getServletContext().getInitParameter("postLogoutURL"));
        }

        return postLogoutURL;
    }

    @Override
    public void destroy() {
        // Nothing needed here
    }
}
