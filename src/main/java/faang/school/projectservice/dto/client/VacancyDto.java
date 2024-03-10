package faang.school.projectservice.dto.client;

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
    private String description;
    private Long createdBy;
    private Integer count;
}
