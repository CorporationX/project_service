package faang.school.projectservice.dto.moment;

import jakarta.validation.constraints.*;
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
    private long id;

    @NotBlank
    @Size(min = 2, max = 90)
    private String name;

    @NotNull
    @Past
    private LocalDateTime date;

    @NotEmpty
    private List<Long> projectsIds;

    @NotNull
    private List<Long> userIds;

    @Size(min = 1, max = 2000)
    private String description;
}
