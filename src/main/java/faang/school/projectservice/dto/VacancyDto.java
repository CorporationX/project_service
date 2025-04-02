package faang.school.projectservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VacancyDto {

    @Positive
    @NotNull
    private Long vacancyId;
    private String coverImageKey;
}
