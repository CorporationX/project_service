package faang.school.projectservice.dto.stage;


import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.StageRoles;
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
    private Project project;
    @NotEmpty (message = "Этап проект с незаполненными ролями или участниками")
    private List<StageRoles> stageRoles;
    @NotNull
    private List<Task> tasks;
    @NotEmpty(message = "Этап проект с незаполненными ролями или участниками")
    private List<TeamMember> executors;

}

