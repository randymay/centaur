package org.blaazin.centaur.web.user.filter;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Sets the Current User object as well as Login and Logout URLs to the Request
 */
public class CentaurUserFilter extends OncePerRequestFilter {

    private String postLoginURL = null;
    private String postLogoutURL = null;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        UserService userService = UserServiceFactory.getUserService();
        User currentUser = userService.getCurrentUser();

        request.setAttribute("loginURL", userService.createLoginURL(createPostLoginURL(request)));
        request.setAttribute("logoutURL", userService.createLogoutURL(createPostLoginURL(request)));
        request.setAttribute("currentUser", currentUser);

        filterChain.doFilter(request, response);
    }

    public String createPostLoginURL(HttpServletRequest request) {
        if (getPostLoginURL() == null) {
            setPostLoginURL(request.getServletContext().getInitParameter("postLoginURL"));
        }

        return getPostLoginURL();
    }

    public String createPostLogoutURL(HttpServletRequest request) {
        if (getPostLogoutURL() == null) {
            setPostLogoutURL(request.getServletContext().getInitParameter("postLogoutURL"));
        }

        return getPostLogoutURL();
    }

    public String getPostLoginURL() {
        return postLoginURL;
    }

    public void setPostLoginURL(String postLoginURL) {
        this.postLoginURL = postLoginURL;
    }

    public String getPostLogoutURL() {
        return postLogoutURL;
    }

    public void setPostLogoutURL(String postLogoutURL) {
        this.postLogoutURL = postLogoutURL;
    }
}
