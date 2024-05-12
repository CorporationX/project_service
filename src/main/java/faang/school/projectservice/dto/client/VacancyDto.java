package faang.school.projectservice.dto.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VacancyDto {
    private long id;
    private String name;
    private String description;
    private long projectId;
    private long createdBy;
    private int count;
}
