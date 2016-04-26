package com.blaazinsoftware.centaur.web.filter.user;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Sets the Current User object as well as Login and Logout URLs to the Request
 */
public class CentaurUserFilter implements Filter {

    public static final String LOGIN_URL_PARAMETER_NAME = "loginURL";
    public static final String LOGOUT_URL_PARAMETER_NAME = "logoutURL";
    public static final String CURRENT_USER_PARAMETER_NAME = "currentUser";
    public static final String CURRENT_USER_ADMIN_PARAMETER_NAME = "currentUserIsAdmin";
    public static final String CURRENT_USER_LOGGED_IN_PARAMETER_NAME = "currentUserIsLoggedIn";

    private static final UserService userService = UserServiceFactory.getUserService();
    private static final String LAST_URL_INDICATOR = "~";
    private static final String POST_LOGIN_URL_PARAM = "postLoginURL";
    private static final String POST_LOGOUT_URL_PARAM = "postLogoutURL";

    private String postLoginURL = null;
    private String postLogoutURL = null;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        postLoginURL = filterConfig.getInitParameter(POST_LOGIN_URL_PARAM);
        if (null == postLoginURL) {
            throw new ServletException(POST_LOGIN_URL_PARAM + " is missing.  If you want to redirect the User to the current URL, set this value to " + LAST_URL_INDICATOR);
        }
        postLogoutURL = filterConfig.getInitParameter(POST_LOGOUT_URL_PARAM);
        if (null == postLogoutURL) {
            throw new ServletException(POST_LOGOUT_URL_PARAM + " is missing.  Set this value to the URL to direct the User to when the log out.");
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        User currentUser = userService.getCurrentUser();

        request.setAttribute(LOGIN_URL_PARAMETER_NAME, createPostLoginURL((HttpServletRequest) request));
        request.setAttribute(LOGOUT_URL_PARAMETER_NAME, createPostLogoutURL());
        request.setAttribute(CURRENT_USER_PARAMETER_NAME, currentUser);
        request.setAttribute(CURRENT_USER_LOGGED_IN_PARAMETER_NAME, userService.isUserLoggedIn());
        if (userService.isUserLoggedIn() && userService.isUserAdmin()) {
            request.setAttribute(CURRENT_USER_ADMIN_PARAMETER_NAME, userService.isUserAdmin());
        } else {
            request.setAttribute(CURRENT_USER_ADMIN_PARAMETER_NAME, null);
        }
        chain.doFilter(request, response);
    }

    public String createPostLoginURL(HttpServletRequest request) {
        if (LAST_URL_INDICATOR.equals(postLoginURL)) {
            // Return to the current URL
            postLoginURL = getFullURL(request);
        }

        return userService.createLoginURL(postLoginURL);
    }

    protected String getFullURL(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();

        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }

    public String createPostLogoutURL() {
        return userService.createLogoutURL(postLogoutURL);
    }

    @Override
    public void destroy() {
        // Nothing needed here
    }
}
