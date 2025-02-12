package faang.school.projectservice.config.context.jira;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JiraAuthFilter implements Filter {
    private final JiraAuthContext jiraAuthContext;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String username = httpRequest.getHeader("x-jira-username");
        String password = httpRequest.getHeader("x-jira-password");
        String baseUrl = httpRequest.getHeader("x-jira-base-url");

        if (username != null && password != null && baseUrl != null) {
            jiraAuthContext.setAuthData(username, password, baseUrl);
        }

        try {
            chain.doFilter(request, response);
        } finally {
            jiraAuthContext.clear();
        }
    }
}