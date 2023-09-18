package faang.school.projectservice.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Validated
public class VacancyFilterDto {

    @NotNull
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String position;
}
