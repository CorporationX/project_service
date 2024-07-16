package faang.school.projectservice.dto.moment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MomentDto {
    private Long id;
    @NotNull
    @NotBlank
    private String name;
    private String description;
    private LocalDateTime date;
    private List<Long> resourcesId;
    @NotNull
    private List<Long> projectsId;
    private List<Long> userIds;
    private String imageId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
