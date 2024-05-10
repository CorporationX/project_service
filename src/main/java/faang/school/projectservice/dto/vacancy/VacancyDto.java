package faang.school.projectservice.dto.vacancy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VacancyDto {
    private Long id;
    private String name;
    private String description;
    private Long projectId;
    private String status;
    private Double salary;
    private List<Long> requiredSkillsIds;
    private List<Long> candidatesIds;
}
