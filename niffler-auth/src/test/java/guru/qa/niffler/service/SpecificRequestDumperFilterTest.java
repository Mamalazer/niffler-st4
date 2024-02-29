package guru.qa.niffler.service;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecificRequestDumperFilterTest {

    @Test
    void doFilterWhenRequestInstanceofHttpServletRequest(@Mock ServletResponse response,
                                                         @Mock FilterChain chain,
                                                         @Mock GenericFilter decorate,
                                                         @Mock HttpServletRequest hRequest) throws IOException, ServletException {
        String urlPattern = "url";
        SpecificRequestDumperFilter specificRequestDumperFilter = new SpecificRequestDumperFilter(decorate, urlPattern);

        when(hRequest.getRequestURI())
                .thenReturn(urlPattern);

        specificRequestDumperFilter.doFilter(hRequest, response, chain);

        verify(decorate, times(1)).doFilter(hRequest, response, chain);
        verify(chain, times(0)).doFilter(hRequest, response);
    }

    @Test
    void doFilterWhenRequestIsNotInstanceofHttpServletRequest(@Mock ServletResponse response,
                                                              @Mock FilterChain chain,
                                                              @Mock GenericFilter decorate,
                                                              @Mock ServletRequest request) throws IOException, ServletException {
        SpecificRequestDumperFilter specificRequestDumperFilter = new SpecificRequestDumperFilter(decorate, "url");
        specificRequestDumperFilter.doFilter(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
        verify(decorate, times(0)).doFilter(request, response, chain);
    }

    @Test
    void doFilterWhenRequestURINotMatchesUrlPattern(@Mock ServletResponse response,
                                                    @Mock FilterChain chain,
                                                    @Mock GenericFilter decorate,
                                                    @Mock HttpServletRequest hRequest) throws IOException, ServletException {
        SpecificRequestDumperFilter specificRequestDumperFilter = new SpecificRequestDumperFilter(decorate, "url");

        when(hRequest.getRequestURI())
                .thenReturn("url2");

        specificRequestDumperFilter.doFilter(hRequest, response, chain);

        verify(decorate, times(0)).doFilter(hRequest, response, chain);
        verify(chain, times(1)).doFilter(hRequest, response);
    }

    @Test
    void doFilterWhenRequestInstanceofHttpServletRequestWithoutPatterns(@Mock ServletResponse response,
                                                                        @Mock FilterChain chain,
                                                                        @Mock GenericFilter decorate,
                                                                        @Mock HttpServletRequest hRequest) throws IOException, ServletException {
        SpecificRequestDumperFilter specificRequestDumperFilter = new SpecificRequestDumperFilter(decorate);
        specificRequestDumperFilter.doFilter(hRequest, response, chain);

        verify(chain, times(1)).doFilter(hRequest, response);
        verify(decorate, times(0)).doFilter(hRequest, response, chain);
    }

    @Test
    void destroy(@Mock GenericFilter decorate) {
        SpecificRequestDumperFilter specificRequestDumperFilter = new SpecificRequestDumperFilter(decorate, "url");
        specificRequestDumperFilter.destroy();
        verify(decorate, times(1)).destroy();
    }
}