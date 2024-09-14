package faang.school.projectservice.dto.moment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MomentDto {
    private long momentId;
    @NotNull(message = "Name cannot be null")
    @NotBlank(message = "Name cannot be empty")
    private String name;
    private String description;
    private LocalDateTime date;
    private List<Long> projectIds;
}

