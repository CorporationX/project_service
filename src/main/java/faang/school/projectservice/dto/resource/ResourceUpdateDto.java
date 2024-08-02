package faang.school.projectservice.dto.resource;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.validation.annotation.NullableNotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceUpdateDto {
    @NullableNotBlank
    @Size(max = 255)
    private String name;

    private List<TeamRole> newRoles;

    private Boolean isActive;
}
