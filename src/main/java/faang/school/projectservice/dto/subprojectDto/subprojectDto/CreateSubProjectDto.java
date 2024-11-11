package faang.school.projectservice.dto.subprojectDto.subprojectDto;

import faang.school.projectservice.model.Project;
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
public class CreateSubProjectDto {
    Long id;
    @NotNull
    private Long parentID;
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Boolean isPrivate;
    private List<Project> children;
}
