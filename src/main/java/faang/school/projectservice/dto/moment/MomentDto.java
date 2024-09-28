package faang.school.projectservice.dto.moment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MomentDto {
    private Long id;

    @NotNull(message = "Name cannot be null")
    @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Date cannot be null")
    private LocalDateTime date;

    @NotNull(message = "Project ID cannot be null")
    private Long projectId;

    private List<Long> userIds;
}