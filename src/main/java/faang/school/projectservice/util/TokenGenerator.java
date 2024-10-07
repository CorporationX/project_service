package faang.school.projectservice.util;

import java.util.Map;

public interface TokenGenerator {

    String generate(Map<String, String> params);
}
