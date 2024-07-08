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
    private Long id;

    @NotBlank
    @Size(min = 2, max = 50)
    private String name;

    @Size(min = 1, max = 4096)
    private String description;

    @NotNull
    @Past
    private LocalDateTime date;

    @NotEmpty
    private List<Long> projectsIDs;

    private List<Long> userIDs;
}
