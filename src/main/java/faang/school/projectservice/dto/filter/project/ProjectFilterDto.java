package faang.school.projectservice.dto.filter.project;

import faang.school.projectservice.dto.filter.FilterDto;
import faang.school.projectservice.model.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectFilterDto extends FilterDto {
    private String name;
    private ProjectStatus status;
}
