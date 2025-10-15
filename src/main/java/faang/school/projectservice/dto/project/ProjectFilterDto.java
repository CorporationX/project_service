package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectFilterDto {
    private String name;

    @Positive(message = "Project owner id must be positive")
    private Long ownerId;

    @Positive(message = "Parent project id must be positive")
    private Long parentProjectId;

    private ProjectStatus status;
    private ProjectVisibility visibility;
    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;

    @Min(value = 0, message = "Page number cannot be negative")
    private Integer page;

    @Min(value = 1, message = "Page size must be positive")
    @Max(value = 100, message = "Page size cannot exceed 100")
    private Integer size;

    private String sortBy;
    private String sortDirection;

    public Integer getPage() {
        return page != null && page >= 0 ? page : 0;
    }

    public Integer getSize() {
        return size != null && size > 0 && size <= 100 ? size : 10;
    }

    public String getSortBy() {
        return sortBy != null ? sortBy : "createdAt";
    }

    public String getSortDirection() {
        return "ASC".equalsIgnoreCase(sortDirection) ? "ASC" : "DESC";
    }
}
