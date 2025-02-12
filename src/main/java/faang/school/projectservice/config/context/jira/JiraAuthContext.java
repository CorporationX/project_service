package faang.school.projectservice.config.context.jira;

import org.springframework.stereotype.Component;

@Component
public class JiraAuthContext {
    private final ThreadLocal<String> usernameHolder = new ThreadLocal<>();
    private final ThreadLocal<String> passwordHolder = new ThreadLocal<>();
    private final ThreadLocal<String> baseUrlHolder = new ThreadLocal<>();

    public void setAuthData(String username, String password, String baseUrl) {
        usernameHolder.set(username);
        passwordHolder.set(password);
        baseUrlHolder.set(baseUrl);
    }

    public String getUsername() {
        return usernameHolder.get();
    }

    public String getPassword() {
        return passwordHolder.get();
    }

    public String getBaseUrl() {
        return baseUrlHolder.get();
    }

    public void clear() {
        usernameHolder.remove();
        passwordHolder.remove();
        baseUrlHolder.remove();
    }
}