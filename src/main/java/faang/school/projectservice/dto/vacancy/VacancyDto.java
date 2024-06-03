package faang.school.projectservice.dto.vacancy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VacancyDto {
    private Long id;
    private String name;
    private String description;
    private Long projectId;
    private Long createdBy;
    private Integer count;
    private List<Long> candidatesIds;
}