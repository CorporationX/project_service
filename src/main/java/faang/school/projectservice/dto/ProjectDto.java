package faang.school.projectservice.dto;

import faang.school.projectservice.model.ProjectStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
public class ProjectDto extends AbstractProjectDto {
    private Long id;
    private Long parentProject;
    private String description;
    private BigInteger storageSize;
    private BigInteger maxStorageSize;
    private List<Long> children;
    private List<Long> stages;
    private List<Long> tasks;
    private ProjectStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Override
    public void validateCommonFields() {
        super.validateCommonFields();
        if (status == null) {
            throw new IllegalArgumentException("Project status is required");
        }
    }
}
