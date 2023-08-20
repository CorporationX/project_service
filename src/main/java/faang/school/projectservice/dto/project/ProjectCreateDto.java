package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.project.ProjectStatus;
import faang.school.projectservice.model.project.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectCreateDto {
    @NotBlank(message = "Name should not be blank")
    @Size(max = 128, message = "The name must be up to 128 characters.")
    private String name;
    @Size(max = 255,message = "Description length should not exceed 255 characters")
    private String description;
    private BigInteger storageSize;
    private BigInteger maxStorageSize;
    private Long ownerId;
    private Long parentProjectId;
    private List<Long> childrenId;
    private List<Long> resourcesId;
    private ProjectVisibility visibility;
    private ProjectStatus status;
    private List<Long> teamsId;
}
