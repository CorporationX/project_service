package faang.school.projectservice.dto;

import faang.school.projectservice.model.WorkSchedule;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateVacancyDto {

    @Size(max = 3000)
    private String description;

    @Positive
    private Double salary;

    @Enumerated(EnumType.STRING)
    private WorkSchedule workSchedule;

    @Min(1)
    private Integer count;
}
