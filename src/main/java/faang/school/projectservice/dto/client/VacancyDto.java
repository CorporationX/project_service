package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VacancyDto {
    private Long id;
    private String name;
    private Long projectId;
}
