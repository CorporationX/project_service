package faang.school.projectservice.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Details of a single field validation error")
public record ValidationErrorDetail(

        @Schema(description = "Name of the field that failed validation")
        String field,

        @Schema(description = "Validation error message")
        String message,

        @Schema(description = "The value that was rejected")
        Object rejectedValue
) {
}
