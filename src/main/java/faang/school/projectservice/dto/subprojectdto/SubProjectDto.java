package faang.school.projectservice.dto.subprojectdto;

import faang.school.projectservice.dto.validate.New;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import org.hibernate.sql.Update;

import java.time.LocalDateTime;

@Data
public class SubProjectDto {

    @NotNull(groups = {Update.class})
    @Null(groups = {New.class})
    private Long id;

    @NotBlank(message = "specify the project name", groups = {New.class, Update.class})
    private String name;

    private String description;

    @NotNull(message = "specify the parent of the subproject", groups = {New.class, Update.class})
    private Long parentProjectId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @NotNull(message = "indicate the project status", groups = {New.class, Update.class})
    private ProjectStatus status;

    @NotNull(message = "indicate the project visibility", groups = {New.class, Update.class})
    private ProjectVisibility visibility;
}