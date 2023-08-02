package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.WorkSchedule;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class VacancyDtoGetReq extends VacancyDto {
    private List<Long> candidatesId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long updatedBy;
    private Double salary;
    private WorkSchedule workSchedule;
    private List<Long> requiredSkillIds;
}
