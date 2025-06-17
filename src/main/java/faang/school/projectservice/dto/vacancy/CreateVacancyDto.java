package faang.school.projectservice.dto.vacancy;

import com.fasterxml.jackson.annotation.JsonInclude;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateVacancyDto {
    @NotBlank(message = "Name must not be blank")
    private String name;
    @NotBlank(message = "description must not be blank")
    private String description;
    @NotNull(message = "projectId must not be Null")
    private Long projectId;
    @NotNull(message = "position must not be Null")
    private TeamRole position;
    @NotNull(message = "count must not be Null")
    @Min(1)
    private Integer count;
    private String status;
}