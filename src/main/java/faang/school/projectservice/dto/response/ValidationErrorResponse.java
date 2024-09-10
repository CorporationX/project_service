package faang.school.projectservice.dto.response;

import java.util.Map;

public record ValidationErrorResponse(Map<String, String> errorMessages) { }
