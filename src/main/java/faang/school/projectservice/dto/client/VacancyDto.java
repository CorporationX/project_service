package faang.school.projectservice.dto.client;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VacancyDto {
    private Long id;
    private String name;
    private String description;
    private Long projectId;
    private Long createdBy;
    private Long count;
}