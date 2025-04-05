package faang.school.projectservice.dto.resource;

import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDto {

    @NotNull(message = "Resource ID cannot be null")
    @Positive(message = "Resource ID must be positive")
    private Long id;

    @NotBlank(message = "Resource name cannot be empty")
    private String name;

    @NotNull(message = "Resource size cannot be null")
    @Positive(message = "Resource size must be positive")
    private BigInteger size;

    @NotNull(message = "Allowed roles cannot be null")
    private List<TeamRole> allowedRoles;

    @NotNull(message = "Resource type cannot be null")
    private ResourceType type;

    @NotNull(message = "Resource status cannot be null")
    private ResourceStatus status;

    @NotNull(message = "Project ID cannot be null")
    private Long projectId;
}
