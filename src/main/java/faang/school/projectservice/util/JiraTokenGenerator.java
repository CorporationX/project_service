package faang.school.projectservice.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class JiraTokenGenerator {
    public static String generate(String email, String token) {
        String credentials = email + ":" + token;
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }
}
