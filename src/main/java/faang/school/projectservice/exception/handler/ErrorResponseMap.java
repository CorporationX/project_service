package faang.school.projectservice.exception.handler;

import java.util.List;
import java.util.Map;

public record ErrorResponseMap(
        String description,
        Map<String, List<String>> errors
) {
}