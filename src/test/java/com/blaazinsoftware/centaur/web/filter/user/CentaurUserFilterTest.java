package com.blaazinsoftware.centaur.web.filter.user;

import com.google.appengine.api.users.User;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

import static org.mockito.Mockito.*;

public class CentaurUserFilterTest {

    private static final String CURRENT_USER = "currentUser";

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalUserServiceTestConfig())
                    .setEnvIsAdmin(false)
                    .setEnvIsLoggedIn(true)
                    .setEnvEmail(CURRENT_USER)
                    .setEnvAuthDomain("AuthDomain");

    @Mock
    private FilterConfig filterConfig;

    private CentaurUserFilter filter = new CentaurUserFilter();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testDoFilterWithParamsSet() throws Exception {
        final String LOGIN_URL = "loginUrl";
        final String LOGOUT_URL = "logoutUrl";

        HttpServletRequest request = mock(HttpServletRequest.class);
        doNothing().when(request).setAttribute(anyString(), any());
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(filterConfig.getInitParameter("postLoginURL")).thenReturn(LOGIN_URL);
        when(filterConfig.getInitParameter("postLogoutURL")).thenReturn(LOGOUT_URL);

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);

        class UserMatcher extends ArgumentMatcher<User> {
            @Override
            public boolean matches(Object argument) {
                if (argument instanceof User) {
                    User user = (User)argument;
                    return CURRENT_USER.equals(user.getEmail());
                }
                return false;
            }
        }

        verify(request, times(1)).setAttribute(CentaurUserFilter.LOGIN_URL_PARAMETER_NAME, "/_ah/login?continue=" + LOGIN_URL);
        verify(request, times(1)).setAttribute(CentaurUserFilter.LOGOUT_URL_PARAMETER_NAME, "/_ah/logout?continue=" + LOGOUT_URL);
        verify(request, times(1)).setAttribute(eq(CentaurUserFilter.CURRENT_USER_PARAMETER_NAME), argThat(new UserMatcher()));
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void testDoFilterWithTypicalParamsSetWithQueryString() throws Exception {
        final String loginUrl = "~";
        final String logoutUrl = "logoutUrl";
        final String currentUrl = "currentUrl";
        final String requestUrl = "http://127.0.0.1:8080/";

        HttpServletRequest request = mock(HttpServletRequest.class);
        doNothing().when(request).setAttribute(anyString(), any());
        when(request.getRequestURL()).thenReturn(new StringBuffer(requestUrl));
        when(request.getQueryString()).thenReturn(currentUrl);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(filterConfig.getInitParameter("postLoginURL")).thenReturn(loginUrl);
        when(filterConfig.getInitParameter("postLogoutURL")).thenReturn(logoutUrl);

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);

        class UserMatcher extends ArgumentMatcher<User> {
            @Override
            public boolean matches(Object argument) {
                if (argument instanceof User) {
                    User user = (User)argument;
                    return CURRENT_USER.equals(user.getEmail());
                }
                return false;
            }
        }

        String encodedRequestUrl = URLEncoder.encode(requestUrl + "?", "UTF-8");

        verify(request, times(1)).setAttribute(CentaurUserFilter.LOGIN_URL_PARAMETER_NAME, "/_ah/login?continue=" + encodedRequestUrl + currentUrl);
        verify(request, times(1)).setAttribute(CentaurUserFilter.LOGOUT_URL_PARAMETER_NAME, "/_ah/logout?continue=" + logoutUrl);
        verify(request, times(1)).setAttribute(eq(CentaurUserFilter.CURRENT_USER_PARAMETER_NAME), argThat(new UserMatcher()));
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void testDoFilterWithTypicalParamsSetWithoutQueryString() throws Exception {
        final String loginUrl = "~";
        final String logoutUrl = "logoutUrl";
        final String currentUrl = "currentUrl";
        final String requestUrl = "http://127.0.0.1:8080/";

        HttpServletRequest request = mock(HttpServletRequest.class);
        doNothing().when(request).setAttribute(anyString(), any());
        when(request.getRequestURL()).thenReturn(new StringBuffer(requestUrl));
        when(request.getQueryString()).thenReturn(null);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(filterConfig.getInitParameter("postLoginURL")).thenReturn(loginUrl);
        when(filterConfig.getInitParameter("postLogoutURL")).thenReturn(logoutUrl);

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);

        class UserMatcher extends ArgumentMatcher<User> {
            @Override
            public boolean matches(Object argument) {
                if (argument instanceof User) {
                    User user = (User)argument;
                    return CURRENT_USER.equals(user.getEmail());
                }
                return false;
            }
        }

        String encodedRequestUrl = URLEncoder.encode(requestUrl, "UTF-8");

        verify(request, times(1)).setAttribute(CentaurUserFilter.LOGIN_URL_PARAMETER_NAME, "/_ah/login?continue=" + encodedRequestUrl);
        verify(request, times(1)).setAttribute(CentaurUserFilter.LOGOUT_URL_PARAMETER_NAME, "/_ah/logout?continue=" + logoutUrl);
        verify(request, times(1)).setAttribute(eq(CentaurUserFilter.CURRENT_USER_PARAMETER_NAME), argThat(new UserMatcher()));
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test(expected = ServletException.class)
    public void testDoFilterWithNoLoginURLParamSet() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        doNothing().when(request).setAttribute(anyString(), any());
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(filterConfig.getInitParameter("postLoginURL")).thenReturn(null);
        when(filterConfig.getInitParameter("postLogoutURL")).thenReturn("/someValue");

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
    }

    @Test(expected = ServletException.class)
    public void testDoFilterWithNoLogoutURLParamSet() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        doNothing().when(request).setAttribute(anyString(), any());
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(filterConfig.getInitParameter("postLoginURL")).thenReturn("/someValue");
        when(filterConfig.getInitParameter("postLogoutURL")).thenReturn(null);

        filter.init(filterConfig);
        filter.doFilter(request, response, filterChain);
    }
}