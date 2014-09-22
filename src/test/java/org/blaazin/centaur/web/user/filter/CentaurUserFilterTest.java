package org.blaazin.centaur.web.user.filter;

import com.google.appengine.api.users.User;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import org.blaazin.centaur.service.CentaurService;
import org.blaazin.centaur.service.CentaurServiceFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;

public class CentaurUserFilterTest {

    private static final String LOGIN_URL = "loginUrl";
    private static final String LOGOUT_URL = "logoutUrl";
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
        when(filterConfig.getInitParameter("postLoginURL")).thenReturn(LOGIN_URL);
        when(filterConfig.getInitParameter("postLogoutURL")).thenReturn(LOGOUT_URL);

        filter.init(filterConfig);

        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testDoFilter() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        doNothing().when(request).setAttribute(anyString(), any());
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

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
    }
}