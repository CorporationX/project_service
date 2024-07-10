package faang.school.projectservice.dto.vacancy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VacancyDto {

    private Long id;
    private String name;
    private String description;
    private Long projectId;
    private List<Long> candidatesIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status;
    private Double salary;
    private String workSchedule;
    private List<Long> requiredSkillIds;
}
