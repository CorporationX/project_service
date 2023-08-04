package faang.school.projectservice.dto.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProjectUpdateDto {
    private Long id;
    @Size(max = 4096, message = "Project's description length can't be more than 4096 symbols")
    private String description;
    private String name;
    @Min(value = 1, message = "Owner id cant be less then 1")
    private long ownerId;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    private ProjectVisibility visibility;
    private ProjectStatus status;
}