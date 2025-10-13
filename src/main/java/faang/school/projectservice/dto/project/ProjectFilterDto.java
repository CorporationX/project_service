package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
    private Long ownerId;
    private Long parentProjectId;
    private ProjectStatus status;
    private ProjectVisibility visibility;
    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;

    // Параметры пагинации
    private Integer page;
    private Integer size;

    // Параметры сортировки
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
