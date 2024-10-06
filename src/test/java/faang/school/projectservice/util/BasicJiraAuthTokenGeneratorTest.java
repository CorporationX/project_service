package faang.school.projectservice.util;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BasicJiraAuthTokenGeneratorTest {

    private final BasicJiraAuthTokenGenerator tokenGenerator = new BasicJiraAuthTokenGenerator();

    @Test
    void generate() {
        Map<String, String> map = Map.of(
                "email", "email@java.com",
                "token", "token"
        );
        String correctToken = "Basic ZW1haWxAamF2YS5jb206dG9rZW4=";

        String result = tokenGenerator.generate(map);

        assertEquals(correctToken, result);
    }
}