package faang.school.projectservice.dto.vacancy;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloseVacancyDto {
    @NotNull
    @Size(min = 1)
    private List<Long> selectedCandidateIds;
}