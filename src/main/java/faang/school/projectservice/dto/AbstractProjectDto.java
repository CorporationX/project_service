package faang.school.projectservice.dto;

import faang.school.projectservice.model.ProjectVisibility;
import lombok.Data;

@Data
public abstract class AbstractProjectDto {
    private String name;
    private Long ownerId;
    private ProjectVisibility visibility;

    public void validateCommonFields() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name project is required");
        }
        if (ownerId == null) {
            throw new IllegalArgumentException("Owner is required");
        }
        if (visibility == null) {
            throw new IllegalArgumentException("Visibility project is required");
        }
    }
}
