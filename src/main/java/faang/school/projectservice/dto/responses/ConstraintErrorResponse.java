package faang.school.projectservice.dto.responses;

import java.util.List;

public record ConstraintErrorResponse(List<Violation> violations) {
}