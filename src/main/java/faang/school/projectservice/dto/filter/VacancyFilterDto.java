package faang.school.projectservice.dto.filter;

import faang.school.projectservice.model.VacancyStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VacancyFilterDto {

    @NotBlank
    @Size(max = 50)
    private String namePattern;
    private VacancyStatus statusPattern;
}
