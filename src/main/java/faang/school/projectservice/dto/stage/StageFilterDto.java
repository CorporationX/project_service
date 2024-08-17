package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StageFilterDto {

    private ProjectStatus projectStatus;
}
