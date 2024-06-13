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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MomentDto {
    private Long id;
    @NotBlank(message = "Moment name can't be blank")
    private String name;
    private String description;
    private LocalDateTime date;
    private List<@NotNull Long> projects;
    private List<@NotNull Long> userIds;
}