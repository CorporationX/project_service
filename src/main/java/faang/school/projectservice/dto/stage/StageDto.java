package faang.school.projectservice.dto.stage;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StageDto {

    private Long stageId;
    @NotNull
    private String stageName;
    @NotEmpty(message = "Проект обязателен при создании этапа, не забудьте заполнить его")
    private Long projectId;
    @NotEmpty (message = "Этап проект с незаполненными ролями или участниками")
    private List<Long> stageRolesIds;
    @NotNull
    private List<Long> tasksIds;
    @NotEmpty(message = "Этап проект с незаполненными ролями или участниками")
    private List<Long> executorsIds;

}

