package faang.school.projectservice.config.context;

import org.springframework.stereotype.Component;

@Component
public class JiraContext {

    private final ThreadLocal<String> email = new ThreadLocal<>();
    private final ThreadLocal<String> token = new ThreadLocal<>();

    public void set(String emailValue, String tokenValue) {
        email.set(emailValue);
        token.set(tokenValue);
    }

    public String getEmail() {
        return email.get();
    }

    public String getToken() {
        return token.get();
    }

    public void clear() {
        email.remove();
        token.remove();
    }
}
