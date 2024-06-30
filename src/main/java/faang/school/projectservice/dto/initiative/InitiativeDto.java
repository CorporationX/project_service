package faang.school.projectservice.dto.initiative;

import faang.school.projectservice.model.initiative.InitiativeStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InitiativeDto {

    @Positive(message = "Id should be positive")
    @NotNull(message = "Id should not be null")
    private Long id;

    @NotNull(message = "Name should not be null")
    @NotBlank(message = "Name should not be blank")
    @Length(max = 64, message = "Name length should be less than 64")
    private String name;

    @NotNull(message = "Description should not be null")
    @NotBlank(message = "Description should not be blank")
    @Length(max = 4096, message = "Description length should be less than 4096")
    private String description;

    @NotNull(message = "CuratorId should not be null")
    @Positive(message = "CuratorId should be positive")
    private Long curatorId;

    @Positive(message = "ProjectId should be positive")
    @NotNull(message = "ProjectId should not be null")
    private Long projectId;

    @NotNull(message = "Status should not be null")
    private InitiativeStatus status;

    private List<Long> stageIds;
}
