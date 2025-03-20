package faang.school.projectservice.dto;

import faang.school.projectservice.model.ProjectStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
public class CreateSubProjectDto extends AbstractProjectDto {
    private Long id;
    private Long parentProject;
    private String description;
    private List<CreateSubProjectDto> children;
    private List<String> stages;
    private ProjectStatus status;

    @Override
    public void validateCommonFields() {
        super.validateCommonFields();
        if (parentProject == null) {
            throw new IllegalArgumentException("Parent project is required");
        }
    }
}
