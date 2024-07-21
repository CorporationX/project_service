package faang.school.projectservice.dto.moment;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MomentDto {
    private Long id;
    @NotBlank
    private String name;
    private String description;
    @NotNull
    private LocalDateTime date;
    private List<Long> projectsId;
    private List<Long> usersId;
}
