package faang.school.projectservice.dto.Vacancy;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@NotNull
public class VacancyFilterDto {
        private String namePattern;
        private String descriptionPattern;
}
