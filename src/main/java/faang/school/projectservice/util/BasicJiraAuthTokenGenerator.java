package faang.school.projectservice.util;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Component
public class BasicJiraAuthTokenGenerator implements TokenGenerator {

    @Override
    public String generate(Map<String, String> params) {
        String credentials = "%s:%s".formatted(params.get("email"), params.get("token"));
        byte[] credentialsBytes = credentials.getBytes(StandardCharsets.UTF_8);
        return "Basic " + Base64.getEncoder().encodeToString(credentialsBytes);
    }
}
