package faang.school.projectservice.dto.moment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class momentDto {
    private Long id;
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

    public momentDto(Long id, List<Long> projectsId) {
        this.id = id;
        this.projectsId = projectsId;
    }
}
