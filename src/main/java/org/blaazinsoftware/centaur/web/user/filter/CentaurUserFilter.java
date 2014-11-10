package org.blaazinsoftware.centaur.web.user.filter;

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

    private String postLoginURL = null;
    private String postLogoutURL = null;

    private FilterConfig filterConfig = null;

    private boolean returnToLastUrl = false;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        UserService userService = UserServiceFactory.getUserService();
        User currentUser = userService.getCurrentUser();

        request.setAttribute(LOGIN_URL_PARAMETER_NAME, userService.createLoginURL(createPostLoginURL((HttpServletRequest)request)));
        request.setAttribute(LOGOUT_URL_PARAMETER_NAME, userService.createLogoutURL(createPostLogoutURL()));
        request.setAttribute(CURRENT_USER_PARAMETER_NAME, currentUser);

        chain.doFilter(request, response);
    }

    public String createPostLoginURL(HttpServletRequest request) {
        if (postLoginURL == null) {
            postLoginURL = (filterConfig.getInitParameter("postLoginURL"));
        } else if ("~".equals(postLoginURL) || returnToLastUrl) {
            returnToLastUrl = true;
            // Return to this URL

            postLoginURL = getFullURL(request);
        }

        return postLoginURL;
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
        if (postLogoutURL == null) {
            postLogoutURL = (filterConfig.getInitParameter("postLogoutURL"));
        }

        return postLogoutURL;
    }

    @Override
    public void destroy() {
        // Nothing needed here
    }
}
