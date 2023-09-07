package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.VacancyStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class VacancyDto {

    @NotNull
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Long projectId;

    @Min(1)
    private List<Long> candidatesId;

    private LocalDateTime createdAt;

    private VacancyStatus status;

    @NotNull
    private Long teamMemberId;

    @NotNull
    private Long createdBy;

    @NotNull
    private Double salary;
}
