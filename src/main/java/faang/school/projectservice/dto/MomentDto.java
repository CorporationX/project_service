package faang.school.projectservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MomentDto {
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    private LocalDateTime date;

    @NotNull(message = "Projects are required")
    @Size(min = 1, message = "At least one project is required")
    private List<Long> projectIds;

    private String imageId;
}
