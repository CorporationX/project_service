package faang.school.projectservice.dto.moment;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class MomentDto {
    Long id;
    @NotEmpty String name;
    String description;
    @NotNull LocalDateTime date;
    List<Long> projectIds;
    List<Long> userIds;
}
