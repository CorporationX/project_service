package faang.school.projectservice.dto;

import faang.school.projectservice.model.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubProjectFilterDto {
    private Long projectId;
    private String nameFilter;
    private List<ProjectStatus> statusesFilter;
}
