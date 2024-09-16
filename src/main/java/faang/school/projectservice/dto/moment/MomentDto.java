package faang.school.projectservice.dto.moment;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class MomentDto {
    @PositiveOrZero
    private Long Id;
    private String name;
    private String description;
    private LocalDateTime date;
    private List<Long> projectIds;
}

