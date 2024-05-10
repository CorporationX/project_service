package faang.school.projectservice.dto.client;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VacancyDto {
    private long id;
    private String name;
    private String description;
    private long projectId;
    private long createdBy;
    private int count;
}
