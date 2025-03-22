package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.config.context.UserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class UserContextFilter extends OncePerRequestFilter {
    private final UserContext userContext;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            String userIdHeader = request.getHeader("X-User-Id");
            if (userIdHeader != null) {
                userContext.setUserId(Long.parseLong(userIdHeader));
            }
            chain.doFilter(request, response);
        } finally {
            userContext.clear();
        }
    }
}