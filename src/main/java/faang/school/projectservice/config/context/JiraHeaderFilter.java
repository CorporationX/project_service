package faang.school.projectservice.config.context;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JiraHeaderFilter implements Filter {

    private final JiraContext jiraContext;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filter)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String email = req.getHeader("x-jira-email"), token = req.getHeader("x-jira-token");

        if (email != null && token != null) {
            jiraContext.set(email, token);
        }
        try {
            filter.doFilter(request, response);
        } finally {
            jiraContext.clear();
        }
    }
}
