package faang.school.projectservice.dto.moment;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MomentDto {
    private Long id;
    @NotNull(message = "Moment name cannot be null")
    @NotEmpty(message = "Moment name cannot be empty")
    private String name;
    private String description;
    private LocalDateTime date;
    @NotNull(message = "Project IDs cannot be null")
    private List<Long> projectIds;
    private List<Long> userIds;
    private String imageId;
}