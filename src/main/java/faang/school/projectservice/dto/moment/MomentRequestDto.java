package faang.school.projectservice.dto.moment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MomentRequestDto {
    @Schema(description = "Name of the moment")
    @NotBlank
    private String name;

    @Schema(description = "Description of the moment")
    @NotBlank
    private String description;

    @Schema(description = "Date and time of the moment")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Builder.Default
    private LocalDateTime date = LocalDateTime.now();

    @Schema(description = "List of project ids", example = "[1, 2, 3]")
    @Size(min = 1)
    private List<@Positive Long> projectIds;

    @Schema(description = "List of user ids", example = "[1, 2, 3]")
    @Size(min = 1)
    private List<@Positive Long> userIds;
}
